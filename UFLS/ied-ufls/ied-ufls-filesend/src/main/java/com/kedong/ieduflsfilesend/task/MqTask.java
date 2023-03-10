package com.kedong.ieduflsfilesend.task;

import cn.hutool.core.collection.CollectionUtil;
//import com.kedong.ieduflscommon.entity.RecFileBean;
import com.kedong.ieduflscommon.entity.BackFileBean;
import com.kedong.ieduflscommon.entity.RecFileBean;
import com.kedong.ieduflsfilesend.util.FileSender;
import com.kedong.ieduflsfilesend.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@EnableScheduling
@Component
public class MqTask {

    @Autowired
    private FileSender fileSender;

    @Autowired
    private FileUtil fileUtil;

    private static Map<String, Integer> cachedFails = new HashMap<>();

    //失败后再补发三次
    private static final Integer maxFails = 3;

    private static final Logger logger = LoggerFactory.getLogger(MqTask.class);

    public static ArrayList<String> cachedFiles = new ArrayList<>();

    public static ArrayList<Map<String, Object>> cacheTasks = new ArrayList<>();

    public static ArrayList<String> cachedResponseFiles = new ArrayList<>();

    public static ArrayList<Map<String, Object>> cacheResponseTasks = new ArrayList<>();

    /**
     *最近一次心跳时间
     */
    private static long  heartTimeStamp = 0;

    /**
     *心跳超时1分钟不再发送
     */
    public  static  final  long  HEART_TIMEOUT= 60 * 1000;

    @Value("${cimeFile.rootPath}")
    private String rootPath;

    /**
     * 遍历目录下文件发送到kafka
     * MODEL目录优先发送
     * RESPONSE目录单独定时任务处理
     * @return
     */
    @Scheduled(cron = "* 0/5 * * * ?")
    public void scheduledTask() {
        System.out.println(":***************5分钟开始工作了===scheduledTask=====：");
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + ":***************5分钟开始工作了===scheduledTask=====：" +Thread.currentThread().getName());
        //扫描文件夹下的所有文件
        List<File> files = new ArrayList<>();
        files = FileUtil.scanAllCimeFile(rootPath + File.separator +"other",files);
        if (CollectionUtil.isNotEmpty(files))
            files.stream().filter(f -> !cachedFiles.contains(f.getName())).forEach(this::sendFileAndCacheTask);
        //get result
        getTaskResult(cachedFiles, cacheTasks);
    }

    private void sendFileAndCacheTask(File file) {
        if (!file.getName().startsWith(".")) {
            Future<Boolean> future = sendFile(file);
            if (future != null) {
                cachedFiles.add(file.getName());
                Map<String, Object> taskItem = new HashMap();
                taskItem.put("file", file);
                taskItem.put("task", future);
                cacheTasks.add(taskItem);
            }
        }
   }

    /**
     * 获取发送结果，并处理缓存和文件备份
     * @param cachedFiles
     * @param cacheTasks
     */
   private void  getTaskResult(ArrayList<String>cachedFiles, ArrayList<Map<String, Object>>cacheTasks) {
       logger.info("cachedFiles.size(============================================" + cachedFiles.size());
       logger.info("*********cacheTasks.size(============================================" + cacheTasks.size());
       while (cachedFiles.size() > 0) {
           for (int i = 0; i < cacheTasks.size(); i++) {
               Map item = cacheTasks.get(i);
               File file = (File) item.get("file");
               Future<Boolean> future = (Future<Boolean>) item.get("task");
               if (future.isDone() && cachedFiles.contains(file.getName())) {
                   cachedFiles.remove(file.getName());
                   try {
                       //备份文件
                       logger.info("Backup File Start:" + file.getName());
                       fileUtil.backupFile(file);
                   } catch (Exception e) {
                       e.printStackTrace();
                       logger.error("Backup File Exception:" + file.getName());
                   }
               }
           }
       }
       cacheTasks.clear();
   }

    /**
     * 扫描report目录
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void scheduledTaskForResponseFolder() {
        logger.info("*******开始工作了===scheduledTaskForResponseFolder=====：" +Thread.currentThread());
        System.out.println("*******开始工作了===scheduledTaskForResponseFolder=====：");
        List<File> files = new ArrayList<>();
        files = FileUtil.scanAllCimeFile(rootPath,files);
        if (CollectionUtil.isNotEmpty(files))
            files.stream().filter(f -> !cachedFiles.contains(f.getName())).forEach(this::sendFileAndCacheTask);
        //get result
        getTaskResult(cachedResponseFiles, cacheResponseTasks);
    }

    /**
     * 文件发送，验证是否心跳超时
     * @param file
     * @return
     */
    private Future<Boolean> sendFile(File file) {
        Future<Boolean> senderTask = null;
        //check heart file time
        if (heartTimeStamp == 0 || System.currentTimeMillis() - heartTimeStamp < HEART_TIMEOUT) {
            senderTask = fileSender.sendFileToIedByKafka(file);
        }
        return senderTask;
    }

    /**
     * 应答处理器
     * 收到成功应答删除备份区文件
     * 收到失败应答重试三次，三次之后删除文件
     * @param bean
     */
    public void handleAckMessage(BackFileBean bean) {
        if (bean.getDataType() == "HEART") {
            heartTimeStamp = bean.getAckTime();
        }else {
            if (bean.getResult() == 1) {
                logger.info("ACK success");
                fileUtil.deleteBackupFile(bean.getFileName());
            } else {
                logger.info("ACK fail !!! Retry to send file");

                if (cachedFails.get(bean.getFileName()) != null && cachedFails.get(bean.getFileName()) >= maxFails) {
                    cachedFails.put(bean.getFileName(), null);
                    return;
                }
                //遇到异常计数加一
                cachedFails.merge(bean.getFileName(), 1, Integer::sum);
                File file = new File(rootPath + "_bak" + File.separator + bean.getFileName());
                if (file.exists()) {
                    sendFile(file);
                } else {
                    System.out.println("gan");
                    logger.error("Backup file not exist");
                }
            }
        }
    }

}
