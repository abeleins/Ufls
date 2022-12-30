package com.kedong.ieduflsfilesend.listener;


import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflscommon.entity.BackFileBean;
import com.kedong.ieduflscommon.entity.RecFileBean;
import com.kedong.ieduflsfilesend.task.MqTask;
import com.kedong.ieduflsfilesend.util.FileUtil;
import com.kedong.ieduflsfilesend.util.MessageTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


/**
 * 主题监听
 * @author wangxinxin
 */
@Component
public class TopicsListener {
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MessageTemplate messageTemplate;

    @Autowired
    private MqTask mqTask;

    @Autowired
    private FileUtil fileUtil;

    @Value("${mq.topic.fileTransAck}")
    private String ackTopic;

    @Value("${mq.topic.command}")
    private String commandTopic;

    private static final Logger logger= LoggerFactory.getLogger(TopicsListener.class);

    @KafkaListener(topics = "${mq.topic.fileTransAck}")
    public void listenAckRecord(ConsumerRecord<?,?> record) {
        logger.info("===========接收到的反馈"+record);
        BackFileBean bean= JSON.parseObject(record.value().toString(), BackFileBean.class);
        mqTask.handleAckMessage(bean);
    }

}
