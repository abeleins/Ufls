package com.kedong.ieduflsfilesend.listener;


import CloudPlatform.CloundPlatformMsgConsumer;
import MsgAPI.CloudBusMessage;
import com.alibaba.fastjson2.JSON;
import com.kedong.ieduflscommon.entity.BackFileBean;
import com.kedong.ieduflsfilesend.task.MqTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 主题监听
 * @author wangxinxin
 */
@Component
public class TopicsListener {

    @Autowired
    private MqTask mqTask;

    @Value("${mq.consumer.ack}")
    private String consumerAck;

    @Value("${mq.topic.fileTransAck}")
    private String ackTopic;

    private static final Logger logger= LoggerFactory.getLogger(TopicsListener.class);

    @PostConstruct
    @Async
    public void messageBusListener() {
        new Thread("ufls-listen-ack") {
            @Override
            public void run() {
                CloundPlatformMsgConsumer consumer = new CloundPlatformMsgConsumer();
                CloudBusMessage msg;
                consumer.init(consumerAck);
                consumer.subscribe(ackTopic);
                while (true) {

                    msg = consumer.recv(ackTopic, 1000);

                    try {
                        if (msg != null) {
                            msg.print();
                            BackFileBean bean = JSON.parseObject(msg.getData(), BackFileBean.class);
                            mqTask.handleAckMessage(bean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

    }
}
