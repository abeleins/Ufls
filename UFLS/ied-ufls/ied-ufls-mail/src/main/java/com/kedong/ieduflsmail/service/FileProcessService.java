package com.kedong.ieduflsmail.service;

import com.kedong.ieduflsmail.util.EmailFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@EnableScheduling
@Component
public class FileProcessService {
    @Autowired
    private DmailSend dmailSend;

    @Autowired
    private DmailRecv dmailRecv;

    @Value("${file.localPath}")
    private String rootPath;

    private static final Logger logger = LoggerFactory.getLogger(FileProcessService.class);

    /**
     * 二区往三区发送RESPONSE目录文件
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduledTaskForResponseFolder() {
        String path = rootPath.concat("RESPONSE");
        logger.info("scheduledTaskForResponseFolder===========path======"+path);
        List<File> files = EmailFileUtil.scanDirectory(path);
        logger.info("scheduledTaskForResponseFolder================="+files.size());
        if(files.size() > 0) {
            dmailSend.sendMail(files);
        }
    }

    /**
     * 二区接手三区发送过来的文件存入COMMAND目录
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduledTaskForCommandFolder() {
        String path = rootPath.concat("COMMAND");
        dmailRecv.recvEh(path);
    }


}
