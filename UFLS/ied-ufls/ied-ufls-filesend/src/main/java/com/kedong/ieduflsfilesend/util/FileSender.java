package com.kedong.ieduflsfilesend.util;

import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflsfilesend.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * 文件发送
 * 将文件分包成规定格式发送至Kafka消息总线
 *
 * @author 王祥生
 * @date Wed Feb 24 14:16:26 CST 2021
 */

@Component
public class FileSender {
//    @Autowired
//    public KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MessageTemplate messageTemplate;

    @Value("${mq.topic.fileTrans}")
    private String topic;

    @Value("${cimeFile.rootPath}")
    private  String rootPath;

    private static final Logger logger = LoggerFactory.getLogger(FileSender.class);

    /**
     * 发送文件内容至Kafka消息队列
     * 每一段都需要带上标志开始/传送中/结束
     * @param file
     * @return
     */
    /**
     * 通过一个文件对象，解析内容发给kafka消息队列
     * 文件输入流+缓冲输入流、文件切片、报文对象、初始化报文对象
     * 传输开始/传输中/传输结束标志，分隔了传输过程。
     * 分片内容需要进行base64编码，然后发送。toJSONString对象转化为JSON字符串
     * log记录时间便于分析
     * 发送标志（setRecFileBean，更改报文对象的属性）==>然后发送内容（messageTemplate.send）
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Async
    public Future<Boolean> sendFileToIedByKafka(File file) throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        String fileName = file.getName();
        byte[] dataArr = new byte[RecFileBean.FILE_FRAGMENT_SIZE];//1024*200
        int len;
        int currentNum = 0;
        //生成报文对象，并初始化
        RecFileBean recFileBean = createRecFileBean(file);
        setRecFileBean(recFileBean, null, CommonConstant.TRANS_FLAG_START, null, null);
        //发送开始消息
        messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
        while ((len = bis.read(dataArr)) != -1) {
            byte[] newDataArr;
            if (len != RecFileBean.FILE_FRAGMENT_SIZE) {
                newDataArr = Arrays.copyOf(dataArr, len);
            } else {
                newDataArr = dataArr;
            }
            //当前序号+1
            currentNum++;
            String data = DealFileTool.getBase64Encode(newDataArr);
            setRecFileBean(recFileBean, currentNum,  CommonConstant.TRANS_FLAG_DOING, data, len);
            messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
        }
        bis.close();

        //发送结束标志
        setRecFileBean(recFileBean, null, CommonConstant.TRANS_FLAG_END, null, null);
        messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
        long currentTimeMillis1 = System.currentTimeMillis();
        logger.info("*********Thread= " + Thread.currentThread().getName() + "====filename =" + fileName + "task======================================" + (currentTimeMillis1 - currentTimeMillis) + "ms");
        return new AsyncResult<>(true);
    }

    /**
     * 给RecFileBean的变化属性赋值
     *
     * @param
     * @return
     */
    public void setRecFileBean(RecFileBean recFileBean, Integer currentNum, int flag, String data, Integer curDataSize) {
        recFileBean.setCurrentNum(currentNum);
        recFileBean.setFlag(flag);
        recFileBean.setTimeStamp(new Date());
        recFileBean.setData(data);
        recFileBean.setDigest(SHAUtil.SHA256(data));
        recFileBean.setCurDataSize(curDataSize);
    }


    /**
     * 根据文件信息生成报文对象
     *
     * @param
     * @return
     */
    public RecFileBean createRecFileBean(File file) {
        return new RecFileBean(file, rootPath);
    }
}
