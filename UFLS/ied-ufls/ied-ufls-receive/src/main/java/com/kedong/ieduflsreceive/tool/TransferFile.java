package com.kedong.ieduflsreceive.tool;

import MsgAPI.CloudBusMessage;
import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflscommon.entity.BackFileBean;
import com.kedong.ieduflscommon.entity.RecFileBean;
import com.kedong.ieduflscommon.enums.DataType;
import com.kedong.ieduflscommon.util.DealFileTool;
import com.kedong.ieduflscommon.util.TimeTool;
import com.kedong.ieduflsreceive.cache.CacheTemplate;
import com.kedong.ieduflsreceive.controller.CimeController;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka监听器，文件传输
 * 处理切片报文
 * 合并切片并生成文件
 * 调用入库controller将数据入库
 */
@Component
@EnableScheduling
public class TransferFile {

    @Value("${cimeFile.savePath}")
    private String rootPath;
    @Autowired
    private CacheTemplate redisTemplate;
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MsgProducerTemplate messageTemplate;
    @Value("${mq.topic.fileAck}")
    private String fileAckTopic;
    @Autowired
    private CimeController cimeController;

    private static final Logger logger = LoggerFactory.getLogger(TransferFile.class);

    @Async
    public void kafkaMessageDeal(ConsumerRecord<?, ?> record) {

    }

    /**
     * kafka 接收到的报文交由多线程处理
     *
     * @param message
     */
    @Async
    public void cloudMessageDeal(CloudBusMessage message) {
        String receive = new String(message.getData());
        //获取接收报文，转为对象
        RecFileBean recFileBean = JSON.parseObject(receive, RecFileBean.class);
        //文件传输状态标志位获取
        int flag = recFileBean.getEndFlag();
        //获取文件名
        String fileName = getFileName(recFileBean);
        String path = DealFileTool.getPath(recFileBean.getDataType());
        try {
            //如果缓存中已经存在该数据片.或存在异常信息的数据则不处理
            if(!isInRedis(recFileBean)){
                logger.info("接收消息异常！请检查报文格式是否符合标准");
                return;
            }
            //接收异常信息
            if (flag == 3) {//文件传输异常
                logger.info("文件切片传输异常！请检查传输报文");
                //删除缓存中的异常信息
                redisTemplate.delete(fileName + "Exception");
                //文件传输出错,删除原文件
                new File(rootPath + File.separator + path + File.separator + fileName).delete();

            }

            //消息存入缓存
            int num = recFileBean.getNo();//报文数据  序号
            String Data = recFileBean.getData().getBinData();//存入redis
            redisTemplate.opsForValue().set(fileName + num, Data);

            // 判断文件传输状态
            if (flag == 2) {//传输结束
                //文件传输结束标志
                int totalNums = recFileBean.getTotalNums();
                String dataType = recFileBean.getDataType();
                String opType = recFileBean.getOpType();
                //文件传输结束，读取缓存中的文件数据并写入硬盘
                File newFile =  fileSave(path, fileName, totalNums);
                if(newFile != null && newFile.exists()){
                    //发送成功确认消息
                    sendAckCloudBusMessage(recFileBean, 1);
                    // 执行到此处，则文件已写入本地成功
                    // 开始解析文件
                    cimeController.analysisCimeFile(dataType,opType, newFile);
                }else{
                    //发送失败确认消息
                    sendAckCloudBusMessage(recFileBean, 0);
                }
            }
        } catch (Exception e) {
            // 文件传输及读取的异常处理 存入缓存中
            redisTemplate.opsForValue().set(fileName + "Exception", "Error");
            //发送错误信息
            sendAckCloudBusMessage(recFileBean, 0);
        }
    }

    /**
     * 发送ACK状态给Kafka
     * @param fileInfo
     * @param ack
     */
    public void sendAckCloudBusMessage(RecFileBean fileInfo,  int ack) {
        BackFileBean recFileBean = new BackFileBean();
        recFileBean.setHeadInfo(fileInfo.getHeadInfo());
        recFileBean.setDataType(fileInfo.getDataType());
        recFileBean.setOpType(fileInfo.getOpType());
        recFileBean.setFileName(fileInfo.getData().getFileName());
        recFileBean.setCondition(fileInfo.getCondition());
        recFileBean.setResult(ack);
        String jsonString = JSON.toJSONString(recFileBean);
        String message = ack == 0 ? "失败需重发" : "文件已接收成功";
        messageTemplate.send(fileAckTopic, fileInfo.getData().getFileName() + ":" + message, jsonString);
    }

    private static final Map<String, Map> redisErrorMap = new HashMap<>();

    /**
     * 文件保存至本地，并入库
     *
     * @param path      隔离传输来的路径
     * @param fileName  隔离传输来文件名
     * @param totalNums 文件的总分片数
     */
    public File fileSave(String path, String fileName, int totalNums) throws Exception {
        logger.info("存储路径："+path);
        logger.info("存储文件名："+fileName);
        logger.info("文件总切片数："+totalNums);
        File newFile = null;
        StringBuilder newPath = new StringBuilder(rootPath);

//        // 存在多级目录的情况下，使用如下代码
//        {
//            if ("FILE".equals(path)) {
//                newPath.append(File.separator).append(relativePath);
//            }
//        }

        logger.info(Thread.currentThread() + ":开始写文件：" + fileName + ",path:" + newPath);
        BufferedOutputStream bos = null;
        try {
            newFile = new File(newPath.append(File.separator).append(path).append(File.separator).append(fileName).toString());
            cimeController.mkdir(newFile);
            //从缓存中取出数据解码并写入指定文件
            bos = new BufferedOutputStream(new FileOutputStream(newFile));
            /**
             * 由于是多线程接收Kafka消息报文，为避免出现
             * 最后一条消息的处理速度比前面的处理快的现象，而之前的消息还未存入redis
             * 遍历读取会报错
             * 所以此处让线程等待100ms
             */
            Thread.sleep(100);

            for (int i = 1; i < totalNums + 1; i++) {
                //从缓存中取数据
                String data = redisTemplate.opsForValue().get(fileName + i);
                //将数据转换成字节数组并存入文件中
                bos.write(DealFileTool.getBase64Decode(data));
            }
            bos.close();
            return newFile;
        } catch (Exception e) {
            //写文件时异常处理，抛出异常由上层方法捕获处理
            e.printStackTrace();
            return null;
        } finally {
            //关流
            bos.close();
        }
    }


    private static Map<String, Integer> errorMap = new ConcurrentHashMap<>();

    /**
     * 每五分钟扫描一下出错目录，重新解析文件
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void analysisErrorPath() {
        String errorPath = rootPath + File.separator + "error";
        File[] files = new File(errorPath).listFiles();
        if (files == null || !(files.length > 0))
            return;

        for (File file : files) {
            String absolutePath = file.getAbsolutePath();
            String[] paths = file.getName().split("_");
            if(paths == null||paths.length<5){
                logger.info("错误文件：（"+file.getName()+"）。切个文件名称失败！");
                return;
            }
            String dataType =paths[3];
            String opType = paths[2];

            if (errorMap.get(absolutePath) == null)
                errorMap.put(absolutePath, 1);
            else if (errorMap.get(absolutePath) > 3) {
                file.delete();
                errorMap.put(absolutePath, null);
            }
            try {
                cimeController.analysisCimeFile(dataType,opType, file);
                errorMap.put(absolutePath, null);
//                    file.delete();
                //文件转移至备份路径
                file.renameTo(new File(new StringBuilder()
                        .append(rootPath)
                        .append(File.separator)
                        .append(DealFileTool.getPath(dataType))
                        .append(File.separator)
                        .append("back")
                        .append(File.separator)
                        .append(file.getName()).toString()));
            } catch (Exception e) {
                e.printStackTrace();
                //记录错误次数
                errorMap.put(absolutePath, errorMap.get(absolutePath) + 1);
            }
        }
    }


    public String getFileName(RecFileBean recFileBean ){
        String fileName = "";
        String dataType = recFileBean.getDataType();
        switch (DataType.getDataType(dataType)){
            case HEART:
                fileName = recFileBean.getCondition().getRegionID()+"_"+DataType.HEART.getCode() + "_" + TimeTool.getTimeStr(new Date().getTime(),TimeTool.FORMIT_TIME)+".CIME";
                break;
            case DATA:
            case MODEL:
                fileName = recFileBean.getData().getFileName();
                break;
            default:
                throw new IllegalArgumentException("数据类型解析出错，请检查DataType数据类型是否正确");
        }
        return fileName;
    }
    /**
     * 是否可以存入缓存
     * @return
     */
    public boolean isInRedis(RecFileBean recFileBean){
        boolean result = true;
        //如果缓存中已经存在该数据片的数据则不处理
        int num = recFileBean.getNo();//报文数据  序号
        String fileName = recFileBean.getData().getFileName();
        int flag = recFileBean.getEndFlag();
        //如果缓存中已有该文件的此片数据，则消息不存入缓存
        if (redisTemplate.opsForValue().get(fileName + num) != null) {//报文序号
            result = false;
        }
        //如果缓存中有异常状态，则消息不存入缓存
        if (redisTemplate.opsForValue().get(fileName + "Exception") == null) {//报文序号
            result = false;
        }
        //结束标志异常
        if(flag != 1 || flag != 2 || flag != 3){
            result = false;
        }
        return result;
    }
}
