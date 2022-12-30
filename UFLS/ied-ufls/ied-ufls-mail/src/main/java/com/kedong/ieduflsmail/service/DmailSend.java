package com.kedong.ieduflsmail.service;

import com.kedong.ieduflsmail.util.EmailFileUtil;
import kd.dmail.bean.Dmail;
import kd.dmail.bean.Message;
import kd.dmail.handle.DmailHandle;
import kd.dmail.handle.DmailHandleFactory;
import kd.dmail.socket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

@Component
public class DmailSend {
    // 国调2 ip地址
    @Value("${server.ip}")
    private String ip;
    // 服务端端口号

    @Value("${server.port}")
    private int port;
    // 二区发送用户名
//    @Value("${send.username}")
    private String username = "国调3.自动化";
    // 二区发送登录密码
    @Value("${send.password}")
    private String password;

    @Value("${file.localPath}")
    private String localPath;

//    @Value("${recv.addr}")
    private String receiveAddr = "国调3.Admin";

    private static final Logger logger = LoggerFactory.getLogger(DmailSend.class);
    /**
     *生成EH文件
     */
    public String buildMail(List<File>  files) {
        Dmail mail = new Dmail();
        mail.setTitle("二区到三区");
        mail.setContent("content");
        Vector<String> recvAddrs = new Vector<String>();
        recvAddrs.add(receiveAddr);
        mail.setRecvAddrs(recvAddrs);
        mail.setSendAddr(username);
        Vector<String> filePaths = new Vector<String>();
        for (File file : files) {
            filePaths.add(file.getName());
        }
        mail.setFiles(filePaths);
        StringBuilder msg = new StringBuilder();
        DmailHandle handle = DmailHandleFactory.getInstance();
        String ehFileName = handle.buildEhFile(mail, localPath, msg,null,null);
        logger.info("ehFileName:" + ehFileName);
        return ehFileName;
    }
    /**
     * 发送邮件
     */

    public void sendMail(List<File>  files) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port,username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message message = null;
        String ehFileName = null;
        String ehFilePath = null;
        if(session!=null) {
            Vector<String> attachFilePaths = new Vector<String>();
            //附件路径
            for (File file : files) {
                logger.info("file.getAbsolutePath()===:" + file.getAbsolutePath());
                attachFilePaths.add(file.getAbsolutePath());
            }
            //eh文件路径
            ehFileName = buildMail(files);
            ehFilePath = localPath.concat(ehFileName);
            //会根据eh文件中的地址的描述发送到指定地点
            message = handle.sendDmail(session, ehFilePath, attachFilePaths);
            handle.loginOut(session);
        }

        if(message != null) {
            if (message.getCode() == 1) {
                logger.info("发送成功！");
                //发送成功后删除本地已发送的文件和邮件
                if (ehFileName != null && checkSent(ehFileName) == true){
                    EmailFileUtil.deleteFiles(files);
                    EmailFileUtil.deleteEmailFile(ehFilePath);
                }
            } else {
                logger.info(message.getErrorMsg());
                //doSomething
            }
        } else {
            logger.info("message null");
        }
    }

    /**
     * 检查邮件是否发送成功
     */
    public boolean checkSent(String ehName) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port,username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder errorMsg = new StringBuilder();
        boolean flag = handle.isMailSent(session, ehName, receiveAddr, errorMsg);
        if(flag) {
            System.out.println("发送成功");
        }else {
            System.out.println("发送失败-"+errorMsg.toString());
        }
        return  flag;
    }

}
