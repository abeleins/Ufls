package com.kedong.ieduflsfilesend.util;

import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflscommon.entity.AreaFileBean;
import com.kedong.ieduflscommon.entity.DataFileBean;
import com.kedong.ieduflscommon.entity.RecFileBean;
import com.kedong.ieduflscommon.enums.CityEnum;
import com.kedong.ieduflscommon.util.DealFileTool;
import com.kedong.ieduflsfilesend.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Arrays;
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

    @Value("${file.fragment.size}")
    private static int  fileFragmentSize;

    private static final Logger logger = LoggerFactory.getLogger(FileSender.class);

    /**
     * 发送文件内容至Kafka消息队列
     * 每一段都需要带上标志传送中/结束
     * @param file
     * @return
     */
    /**
     * 通过一个文件对象，解析内容发给kafka消息队列
     * 文件输入流+缓冲输入流、文件切片、报文对象、初始化报文对象
     * 传输中/传输结束标志，分隔了传输过程。
     * 分片内容需要进行base64编码，然后发送。toJSONString对象转化为JSON字符串
     * log记录时间便于分析
     * 发送标志（setRecFileBean，更改报文对象的属性）==>然后发送内容（messageTemplate.send）
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Async
    public Future<Boolean> sendFileToIedByKafka(File file) {
        long currentTimeMillis = System.currentTimeMillis();
        String fileName = file.getName();
        byte[] dataArr = new byte[fileFragmentSize];//1024*200
        int len;
        int currentNum = 1;
        int totalNums = new Long(file.length() / fileFragmentSize).intValue() + 1;
        //生成报文对象
        DataFileBean data = new DataFileBean();
        //创建文件地区对象
        AreaFileBean condition = new AreaFileBean();
        RecFileBean recFileBean = new RecFileBean();
        try{
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            String hostName = InetAddress.getLocalHost().getHostName();
            while ((len = bis.read(dataArr)) != -1) {
                byte[] newDataArr;
                newDataArr = Arrays.copyOf(dataArr, len);
                String datas = DealFileTool.getBase64Encode(newDataArr);
                //当前分片数小于总分片数时，
                if (totalNums > currentNum ) {
                    //根据文件给报文对象赋值
                    recFileBean = createRecFileBean(file,newDataArr.length,currentNum,CommonConstant.TRANS_FLAG_DOING,datas,totalNums,hostName,data,condition,recFileBean);
                    messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
                } else {
                    //发送结束标志
                    recFileBean = createRecFileBean(file,newDataArr.length,currentNum,CommonConstant.TRANS_FLAG_END,datas,totalNums,hostName,data,condition,recFileBean);
                    messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
                }
                //当前序号+1
                currentNum++;
            }
            bis.close();
        } catch (Exception e) {
            recFileBean = createRecFileBean(file,0,currentNum,CommonConstant.TRANS_FLAG_ERROR,null,0,null,null,null,recFileBean);
            messageTemplate.send(topic, fileName, JSON.toJSONString(recFileBean));
            logger.info("*********Thread= " + Thread.currentThread().getName() + "文件发送异常，文件名：" + fileName+"");
        }
        long currentTimeMillis1 = System.currentTimeMillis();
        logger.info("*********Thread= " + Thread.currentThread().getName() + "====filename =" + fileName + "task======================================" + (currentTimeMillis1 - currentTimeMillis) + "ms");
        return new AsyncResult<>(true);
    }

    /**
     * 根据文件信息生成报文对象
     *
     * @param
     * @return
     */
    public RecFileBean createRecFileBean(File file,long curLen,int no,int endFlag,String binData,int totalNums,String hostName,DataFileBean data,AreaFileBean condition,RecFileBean bean) {
        String fileName = file.getName();
        //切分文件名
        String[] split = fileName.split("_");
        String regionID = split[0];
        String regionName = CityEnum.getCity(regionID).getRegionName();
        long totalLen = file.length();
//        long millis = System.currentTimeMillis();
        String headInfo = split[0] + "_" + hostName + "_" + split[5];
        //文件数据对象赋值
        setDataFileBean(data,fileName,totalLen,curLen,0L,binData);
        //文件地区对象赋值
        setAreaFileBean(condition,regionID,regionName);
        setRecFileBean(bean,headInfo,no,totalNums,endFlag,split[3],split[2],condition,data);
        return bean;
    }

    public void setRecFileBean(RecFileBean bean,String headInfo, int no, int totalNums, int endFlag, String dataType, String opType, AreaFileBean condition, DataFileBean data) {
        bean.setHeadInfo(headInfo);
        bean.setNo(no);
        bean.setTotalNums(totalNums);
        bean.setEndFlag(endFlag);
        bean.setDataType(dataType);
        bean.setOpType(opType);
        bean.setCondition(condition);
        bean.setData(data);
    }

    public void setDataFileBean(DataFileBean bean,String fileName, Long totalLen, Long curLen, Long time, String binData) {
        bean.setFileName(fileName);
        bean.setTotalLen(totalLen);
        bean.setCurLen(curLen);
        bean.setTime(time);
        bean.setBinData(binData);
    }

    public void setAreaFileBean(AreaFileBean bean,String regionID, String regionName) {
        bean.setRegionID(regionID);
        bean.setRegionName(regionName);
    }
}
