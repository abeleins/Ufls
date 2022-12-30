package com.kedong.ieduflsfilesend.util;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


@Component
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @Value("${cimeFile.rootPath}")
    private String rootPath;

    /**
     * 扫描所有路径下的文件
     *
     * @return
     */
    public static Map<String, List<File>> scanAllDirectory(String path) {
        String[] folders = {"RESPONSE", "MODEL", "REAL", "EVENT", "FILE"};
        Map<String, List<File>> fileMap = new HashMap<>(folders.length);
        for (int i = 0; i < folders.length; i++) {
            List<File> fileList = new ArrayList<>();
            scanAllCimeFile(path + "/" + folders[i], fileList);
            if (fileList.size() > 0) {
                fileMap.put(folders[i], fileList);
            }
        }
        return fileMap;
    }

    /**
     * 扫描指定路径下的所有CIME文件, 包括子目录下文件(file目录下的文件按厂站和装置名存放)
     *
     * @return
     */
    public static List<File> scanAllCimeFile(String path, List<File> fileList) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        scanAllCimeFile(file2.getAbsolutePath(), fileList);
                    } else {
                        fileList.add(file2);
                    }
                }
            }
        } else {
            logger.info("folder not exist:" + path);
        }
        return fileList;
    }


    /**
     * 备份文件
     *
     * @return
     */
    @Async
    public Future<Boolean> backupFile(File file) {
        String path = rootPath + "_bak/";
        File folder = new File(path);
        if (!folder.exists()) {
            Boolean res = folder.mkdir();
            if (!res) {
                logger.info("folder create fail:" + file.getName());
            }
        }
        String fileName = path + file.getName();
        Boolean res = file.renameTo(new File(fileName));
        return new AsyncResult<Boolean>(res);
    }

    /**
     * 删除备份文件
     *
     * @return
     */
    @Async
    public Future<Boolean> deleteBackupFile(String fileName) {
        File file = new File(rootPath + "_bak/" + fileName);
        Boolean res = true;
        if (file.exists()) {
            res = file.delete();
        } else {
            logger.info("delete backup file not exist:" + fileName);
        }
        return new AsyncResult<Boolean>(res);
    }

    /**
     * 创建命令文件
     *
     * @param bean
     * @return
     */
    @Async
    public Future<Boolean> createCommandFile(RecFileBean bean) {
        String fileName = bean.getFileName();
        try {
            File file = new File(rootPath + "/COMMAND/" + fileName + ".cime");
            //如果已有原文件，则删除，创建新的文件
            if (file.exists())
                file.delete();
            if (!file.createNewFile()) {
                logger.info("create file fail:" + fileName);
            }
            //命令文件或心跳文件中添加内容
            String str;
            if (fileName.contains("Heart"))
                str = "<! System=调控云 Version=1.0 Code=UTF_8 Type= Time='2015_10_25 09:14:05' !>\n" +
                        "<Heart>\n" +
                        "  @ Num        Time\n" +
                        "  // 序号      时间         \n" +
                        " #1      '" + DateUtil.format(bean.getTimeStamp(), "yyyy-MM-dd HH:mm:ss.SSS") + "'\n" +
                        "</Heart>";
            else
                str = "<! System=调控云 Version=1.0 Code=UTF_8 Type= Time='2015_10_25 09:14:05' !>\n" +
                        "<Time>\n" +
                        "  @ Num        Time\n" +
                        "  // 序号      时间         \n" +
                        " #1      '" + DateUtil.format(bean.getTimeStamp(), "yyyy-MM-dd HH:mm:ss.SSS") + "'\n" +
                        "</Time>";
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception e) {
            logger.error("create file exception:" + fileName);
            return new AsyncResult<Boolean>(false);
        }

        return new AsyncResult<Boolean>(true);
    }
}
