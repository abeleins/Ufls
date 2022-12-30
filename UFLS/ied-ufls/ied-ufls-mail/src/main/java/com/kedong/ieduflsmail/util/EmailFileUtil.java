package com.kedong.ieduflsmail.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
public class EmailFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailFileUtil.class);

    @Value("${file.localPath}")
    private String rootPath;

    /**
     * 扫描所有路径下的文件
     *
     * @return
     */
    public static List<File> scanDirectory(String rootPath) {
            List<File> fileList = new ArrayList<>();
            scanAllCimeFile(rootPath, fileList);
        return fileList;
    }

    /**
     * 扫描指定路径下的所有CIME文件, 包括子目录下文件(file目录下的文件按厂站和装置名存放)
     *
     * @return
     */
    public static List<File> scanAllCimeFile(String path, List<File> fileList) {
        File file = new File(path);
        long fileSize = 0;
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
                        //单次文件累计不超过15M
                        if (fileSize + file2.length() <= 15 * 1024 * 1024) {
                            fileSize += file2.length();
                            fileList.add(file2);
                        } else {
                            logger.info("file size:" + file2.length());
                            break;
                        }
                    }
                }
            }
        } else {
            logger.info("folder not exist:" + path);
        }
        return fileList;
    }

    public static boolean deleteFiles(List<File> files) {
        boolean res = true;
        for (File file : files) {
            if (file.exists()) {
                res = file.delete();
            } else {
                logger.info("delete ile not exist:" + file.getName());
            }
        }
        return res;
    }
    public static boolean deleteEmailFile(String filePath) {
        boolean res = true;
        File file = new File(filePath);
        if (file.exists()) {
            res = file.delete();
        } else {
            logger.info("delete file not exist:" + filePath);
        }
        return res;
    }
}
