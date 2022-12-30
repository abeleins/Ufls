package com.kedong.ieduflsreceive.controller;

import com.kedong.iedfilerec.cache.CacheTemplate;
import com.kedong.iedfilerec.cache.SetOperations;
import com.kedong.iedfilerec.service.DataService;
import com.kedong.ieduflscommon.enums.DataType;
import com.kedong.ieduflscommon.enums.OpType;
import com.kedong.ieduflscommon.util.DealFileTool;
import com.kedong.ieduflsreceive.service.DataService;
import com.national.cloud.ied.command.CommandModel;
import com.national.cloud.ied.iedenum.CommandEnum;
import com.national.cloud.ied.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * CIME文件完成本地写入之后，跳用此类方法进行解析入库
 * 2022年12月26日16:52:17
 */
@Component
@EnableScheduling
@EnableAsync
public class CimeController {

    private static final Logger logger = LoggerFactory.getLogger(CimeController.class);

    @Autowired
    private DataService dataService;
    @Value("${cimeFile.savePath}")
    private String rootPath;
    @Autowired
    private CacheTemplate redisTemplate;

    public static final Set<String> recordSet = new HashSet<>();

    /**
     * 解析CIME文件入库
     *
     * @param dataType
     * @param opType
     * @param file
     */
//    @Async
    public void analysisCimeFile(String dataType,String opType, File file) {
        //TODO 模拟发送消息，对路径解析进行测试
        try {

            switch (DataType.getDataType(dataType)){
                case HEART:
                    //心跳文件，解析并更新心跳状态
                    analysisResponseDirectory(file);
                    //解析更新通讯状态后，删除心跳文件
                    file.delete();
                    break;
                case MODEL:
                    //模型文件，解析并入库模型数据
                    analysisModelFile(opType,file);
                    break;
                case DATA:
                    //数据文件，解析并入库数据文件
                    analysisDataFile(opType,file);
                    break;
                default:
                    throw new IllegalArgumentException("文件解析出错，请检查路径是否正确");
            }

            //如果是心跳文件，不做备份处理
            if (file.getName().contains("HEART")){
                return;
            }

            //文件转移到备份路径
            StringBuilder backPath = new StringBuilder();
            backPath.append(rootPath)
                    .append(File.separator)
                    .append(DealFileTool.getPath(dataType))
                    .append(File.separator)
                    .append("back")
                    .append(File.separator)
                    .append(file.getName());

            File bakFile = new File(backPath.toString());
            mkdir(bakFile);
            file.renameTo(bakFile);
        } catch (Exception e) {
            e.printStackTrace();
            //文件转移到报错路径
            File errorFile = new File(new StringBuilder()
                    .append(rootPath)
                    .append(File.separator)
                    .append("error")
                    .append(File.separator)
                    .append(file.getName()).toString());
            mkdir(errorFile);
            //文件转移到报错暂存路径
            file.renameTo(errorFile);
        }

    }
    /**
     * 解析心跳文件
     * 更新省地通信状态
     */
    public void analysisResponseDirectory(File file) throws IOException {
        String fileName = file.getName();
        if (fileName.contains("Heart") || fileName.contains("HEART")) {// 如果是心跳文件，则更新通讯状态
            dataService.updateHeart(file);//更新通信状态
            return;
        }
    }
    /**
     *  //解析模型文件 获取列集合，和数据双层集合
     * @param opType
     * @param file
     */
    public void analysisModelFile(String opType, File file){
        //根据功能类型分类
        switch (OpType.getDataType(opType)){
            case UNIT://装置解析比较特殊，一个文件解析，入库装置表和信号表两个表
                dataService.analyModelUnit(file.getAbsolutePath());
                break;
            case ROUND://入库轮次表
            case ACTITEM://入库动作项表
                break;
        }
    }
    /**
     *  //解析数据文件 获取列集合，和数据双层集合
     * @param opType
     * @param file
     */
    public void analysisDataFile(String opType, File file){
        //根据功能类型分类
        switch (OpType.getDataType(opType)){
            case UNIT://入库装置表
            case ROUND://入库轮次表
            case ACTITEM://入库动作项表
                break;
            case REPORT://动作报告，入库动作报告记录表和动作项记录表
                break;
        }
    }
    /**
     * File 目录下的文件转移到备份路径
     *
     * @param file
     */
//    private void analysisFileDirectory(File file, List<String> recordList) {
//        if (!file.isDirectory())
//            return;
//        File[] files = file.listFiles();
//        for (File oldFile : files) {
//            if (oldFile.isDirectory()) {
//                analysisFileDirectory(oldFile, recordList);
//            } else {
//                String absolutePath = oldFile.getAbsolutePath();
//                recordList.add(DealFileTool.getRecordRelativePathName(absolutePath));
//                String newPath = absolutePath.replace(rootPath, rootPath + "_bak");
//                File newFile = new File(newPath);
//                mkdir(newFile);
//                oldFile.renameTo(newFile);
//            }
//        }
//    }



























    /**
     * 检测是否已经有指定目录，无则新建
     *
     * @param file
     */
    public void mkdir(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdirs();
    }



















    /**
     * 如果文件解析过程中出错，则生成报错的文件名存入redis
     *
     * @param fileName
     * @return
     */
    private String getFailFileNameString(String fileName) {
        int index = fileName.lastIndexOf("_");
        String prefix = fileName.substring(0, index - 1);
        String suffix = fileName.substring(index);
        return prefix + "7" + suffix;
    }

    private static Map<String, Integer> faultReportMap = new HashMap<>();

    /**
     * 定期查询redis，获取故障报告的路径
     * 然后解析入库
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scanRedisFaultReportPath() {

        SetOperations opsForSet = redisTemplate.opsForSet();
        Set<String> faultReportSet = opsForSet.members("faultReportSet");
        StringBuilder stringBuilder = new StringBuilder().append(rootPath).append("_bak")
                .append(File.separator);
        faultReportSet.stream().forEach(k -> {
            StringBuilder path = new StringBuilder().append(stringBuilder).append(k).append(".cime");
            File file = new File(path.toString());
            logger.info("解析故障报告：" + file.getAbsolutePath());
            if (faultReportMap.get(k) == null) {
                faultReportMap.put(k, 0);
            } else if (faultReportMap.get(k) >= 3) {
//              如果已经尝试过三次了，则不再继续尝试
//                file.delete();
                //文件放入出错目录
                File destFile = new File(new StringBuilder()
                        .append(stringBuilder.toString().replace("_bak", "_error"))
                        .append(k).append(".CIME").toString());
                mkdir(destFile);
                file.renameTo(destFile);
                opsForSet.remove("faultReportSet", k);
                faultReportMap.put(k, null);
                return;
            }

            try {
                dataService.insertFaultReport(file, redisTemplate.opsForValue().get(k));
                opsForSet.remove("faultReportSet", k);
                faultReportMap.put(k, null);
                logger.info("解析故障报告完毕:" + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                faultReportMap.put(k, faultReportMap.get(k) + 1);
            }

        });
    }

    private static Map<String, Integer> faultDcReportMap = new HashMap<>();

    /**
     * 定期查询redis，获取直流故障报告的路径
     * 然后解析入库
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scanRedisDcFaultReportPath() {

        SetOperations opsForSet = redisTemplate.opsForSet();
        Set<String> faultReportSet = opsForSet.members("faultDcReportSet");
        StringBuilder stringBuilder = new StringBuilder().append(rootPath).append("_bak")
                .append(File.separator);
        faultReportSet.stream().forEach(k -> {
            StringBuilder path = new StringBuilder().append(stringBuilder).append(k).append(".CIME");
            File file = new File(path.toString());
            logger.info("解析直流故障报告：" + file.getAbsolutePath());
            if (faultDcReportMap.get(k) == null) {
                faultDcReportMap.put(k, 0);
            } else if (faultDcReportMap.get(k) >= 3) {
//              如果已经尝试过三次了，则不再继续尝试
//                file.delete();
                //文件放入出错目录
                File destFile = new File(new StringBuilder()
                        .append(stringBuilder.toString().replace("_bak", "_error"))
                        .append(k).append(".CIME").toString());
                mkdir(destFile);
                file.renameTo(destFile);
                opsForSet.remove("faultDcReportSet", k);
                faultDcReportMap.put(k, null);
                return;
            }

            try {
                dataService.insertDcFaultReport(file);
                opsForSet.remove("faultDcReportSet", k);
                faultDcReportMap.put(k, null);
                logger.info("解析直流故障报告完毕:" + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                faultDcReportMap.put(k, faultDcReportMap.get(k) + 1);
            }

        });
    }

    //更新录波文件更新状态
    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateRecord() {
        List<String> recordList = new ArrayList<>();
        synchronized (recordSet) {
            recordList.addAll(recordSet);
            recordSet.removeAll(recordList);
        }
        //更新录波文件上传状态
        dataService.updateRecordListUpload(recordList);
    }

    /**
     * 定期扫描备份文件夹，并进行处理
     */
    @Scheduled(cron = "0 0 2 1/3 * ?")
    public void scanBackupFile() {
        File file = new File(rootPath + "_bak");
        scanAllBackFilesAndDelete(file);
        //出错文件夹也进行处理
        scanAllBackFilesAndDelete(new File(rootPath + "_error"));
    }

    private long times = 30 * 24 * 60 * 60 * 1000;

    /**
     * 扫描备份文件夹，删除大于30天的文件
     *
     * @param file
     */
    private void scanAllBackFilesAndDelete(File file) {
        if (!file.isDirectory())
            return;
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory())
                scanAllBackFilesAndDelete(f);
            else {
                long lastModified = f.lastModified();
                if (System.currentTimeMillis() - lastModified > times)
                    f.delete();
            }
        }
    }
}
