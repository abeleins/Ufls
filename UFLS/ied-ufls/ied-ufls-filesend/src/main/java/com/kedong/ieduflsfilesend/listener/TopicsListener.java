package com.kedong.ieduflsfilesend.listener;


import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflsfilesend.task.MqTask;
import com.kedong.ieduflsfilesend.util.FileUtil;
import com.kedong.ieduflsfilesend.util.MessageTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

//    @KafkaListener(topics = "${mq.topic.fileTransAck}")
    public void listenAckRecord(ConsumerRecord<?,?> record) {
        logger.info("===========接收到的反馈"+record);
        RecFileBean bean= JSON.parseObject(record.value().toString(),RecFileBean.class);
        mqTask.handleAckMessage(bean);
    }

//    @KafkaListener(topics = "${mq.topic.command}")
    public void listenCommandTopic(ConsumerRecord<?,?> record) {
        logger.info("##########接收到生成命令文件主题是"+record);

        RecFileBean bean= JSON.parseObject(record.value().toString(),RecFileBean.class);
        fileUtil.createCommandFile(bean);
        logger.info("##########接收到生成命令文件主题是====="+bean);
        mqTask.handleHeartMessage(record.timestamp());
    }

    //TODO:: 测试后期删除
//    @KafkaListener(topics = "${mq.topic.fileTrans}")
//    public void listen(ConsumerRecord<?,?> record) throws InterruptedException {
////        System.out.println("接收到的主题是"+record);
//
//        RecFileBean bean= JSON.parseObject(record.value().toString(),RecFileBean.class);
//        if (bean.getFlag().equals(CommonConstant.TRANS_FLAG_END)) {
//            bean.setAck(0);
////            messageTemplate.send(CommonConstant.FILE_TRANS_ACK_TOPIC, bean.getFileName(), JSON.toJSONString(bean));
////
////            Thread.sleep(50000);
//            bean.setAck(1);
//            messageTemplate.send(ackTopic, bean.getFileName(), JSON.toJSONString(bean));
////
//            messageTemplate.send(commandTopic, bean.getFileName(), JSON.toJSONString(bean));
////
////            messageTemplate.send(CommonConstant.FILE_TRANS_COMMAND_TOPIC, bean.getFileName(), JSON.toJSONString(bean));
//        }
//    }


}
