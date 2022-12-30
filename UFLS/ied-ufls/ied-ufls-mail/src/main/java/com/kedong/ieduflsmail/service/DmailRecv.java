package com.kedong.ieduflsmail.service;

import kd.dmail.bean.Dmail;
import kd.dmail.bean.Message;
import kd.dmail.handle.DmailHandle;
import kd.dmail.handle.DmailHandleFactory;
import kd.dmail.socket.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

@Component
public class DmailRecv {
    // 国调2 ip地址
    @Value("${server.ip}")
    private String ip;
    // 服务端端口号
    @Value("${server.port}")
    private int port;

//    @Value("${send.username}")
    private String username = "国调3.Admin";

    @Value("${recv.password}")
    private String password;

    @Value("${file.localPath}")
    private String localPath;

    @Value("${recv.addr}")
    private String sendAddr;

    /**
     * 检查是否收到
     */
    public void checkRecv() {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port, username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ehFile = "DMail_20140227_145841.eh";
        StringBuilder errorMsg = new StringBuilder();
        boolean recvSuccess = handle.isMailRecvd(session, ehFile, errorMsg);

        if(recvSuccess) {
            System.out.println("邮件已收到");
        }else {
            System.out.println("错误信息:"+errorMsg);
        }
    }

    /**
     * 接收eh文件
     */
    public void recvEh(String path) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port, username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = null;
        //存储接收到的邮件名
        Vector<String> ehFileNames = null;
        if (session != null) {
            ehFileNames = new Vector<String>();
            // 第二个参数为接收到的eh文件在本地的存放路径
            message = handle.dmailRecv(session, 2, path, ehFileNames);
            handle.loginOut(session);
        }
        // 接收成功
        if (message != null) {
            if (message.getCode() == 1) {
                System.out.println("eh文件接收成功！");
                // doSomething..
                System.out.println("第一个邮件名：" + ehFileNames.get(0));
                for (String ehFileName : ehFileNames) {
                    if (readMail(ehFileName)) {
                        recvAttach(ehFileName, path);
                    }
                }

            } else {
                System.out.println("接收失败");
                // doSomething..
                System.out.println("错误信息--" + message.getErrorMsg());
            }
        }
    }
    /**
     * 接收eh文件对应的附件
     * 该邮件必须是已读邮件，否则会有EOFException
     */
    public void recvAttach(String ehFileName, String path) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port, username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = new Message();
        if (session != null) {
            Vector<String> v = new Vector<String>();
            // 假设eh文件名为gw1.eh,附件名为，abc.txt和123.txt。此处为从文件中解析出来的

            String filePath = path+"/"+ehFileName;
            System.out.println("path+ehFileName==="+filePath);
            Dmail dmail =  dmailParse(filePath);
            for (String pathStr: dmail.getFiles()){
                v.add(pathStr);
            }
            message = handle.recvDmailAttach(session, ehFileName, v, path);
            handle.loginOut(session);
        }

        // 接收成功
        if (message.getCode()==1) {
            System.out.println("附件接收成功！");
            // doSomething..
            delMail(ehFileName);
        } else {
            System.out.println("接收失败");
            // doSomething..
            System.out.println("错误信息："+message.getErrorMsg());
        }
    }

    /**
     * 阅读邮件
     */
    public boolean readMail(String ehFileName) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port,username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ehFile = ehFileName;
        Message message = null;
        //判断是否通过验证
        if(session!=null) {
            message = handle.readDmail(session, ehFile);
            handle. loginOut (session);
        }

        // 接收成功
        if (message.getCode()==1) {
            System.out.println("阅读邮件成功！");
            // doSomething..

        } else {
            System.out.println("阅读失败");
            // doSomething..
            System.out.println("错误信息："+message.getErrorMsg());
        }
        return message.getCode()==1;
    }

    /**
     * 删除邮件
     */
    public void delMail(String efileName) {
        DmailHandle handle = DmailHandleFactory.getInstance();
        Session session = null;
        try {
            session = handle.login(ip, port,username, password);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ehFile = efileName;
        Message message = null;
        if(session!=null) {
            message = handle.delDmail(session, ehFile);
            handle. loginOut (session);
        }
        if(message.getCode()==1) {
            System.out.println("删除成功");
        }else {
            System.out.println("删除失败！");
            System.out.println("错误信息："+message.getErrorMsg());
        }

        handle.loginOut(session);
    }

    public Dmail dmailParse(String filePath) {
        String ehFilePath = filePath;

        DmailHandle handle = DmailHandleFactory.getInstance();

        StringBuilder errorMsg = new StringBuilder();
        Dmail dmail = handle.dmailParse(ehFilePath, errorMsg);

        //eh文件名
        System.out.println(dmail.getMailName());
        System.out.println(dmail.getTitle());
        System.out.println(dmail.getSendTime());
        System.out.println(dmail.getContent());
        System.out.println(dmail.getTransferType());
        //得到第一个附件名
        System.out.println(dmail.getFiles().get(0));
        //第一个接收地址
        System.out.println(dmail.getRecvAddrs().get(0));
        return dmail;
    }
}
