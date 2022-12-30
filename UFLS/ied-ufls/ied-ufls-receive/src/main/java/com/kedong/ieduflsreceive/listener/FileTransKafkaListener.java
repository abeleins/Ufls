package com.kedong.ieduflsreceive.listener;

import CloudPlatform.CloundPlatformMsgConsumer;
import MsgAPI.CloudBusMessage;
import com.kedong.ieduflsreceive.tool.TransferFile;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 监听消息总线，获取数据文件切片
 */
@Component
public class FileTransKafkaListener {

    @Autowired
    private TransferFile transferFile;
    @Value("${mq.topic.fileReceive}")
    private String iedTopic;
    @Value("${mq.topic.fileReceiveProvince}")
    private String iedProvinceTopic;

    //    ${mq.topic.fileReceive}
    //TODO 正式需取消下面注释
//    @KafkaListener(topics = "${mq.topic.fileReceive}")
    public void kafkaListener(ConsumerRecord<?, ?> record) {
        //接收到的报文处理
        transferFile.kafkaMessageDeal(record);
    }

    //    @KafkaListener(topics = "${mq.topic.fileReceiveProvince}")
    public void kafkaListenerProvince(ConsumerRecord<?, ?> record) {
        //接收到的报文处理
        transferFile.kafkaMessageDeal(record);
    }
//Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
    @PostConstruct
    @Async
    public void messageBusListener() {
        new Thread("ied-listen") {
            @Override
            public void run() {
                CloundPlatformMsgConsumer consumer = new CloundPlatformMsgConsumer();
                CloudBusMessage msg;
                consumer.init("ied_kd_sg");
                consumer.subscribe(iedTopic);
                while (true) {
                    try {
                        msg = consumer.recv(iedTopic, 1000);
                        if (msg != null) {
                            transferFile.cloudMessageDeal(msg);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    /**
     * 网省监听
     */
    @PostConstruct
    public void messageBusProvinceListener() {
        new Thread("ied-province-listen") {
            @Override
            public void run() {
                CloundPlatformMsgConsumer consumer = new CloundPlatformMsgConsumer();
                CloudBusMessage msg;
                consumer.init("ied_kd_province");
                consumer.subscribe(iedProvinceTopic);
                while (true) {
                    try {
                        msg = consumer.recv(iedProvinceTopic, 1000);
                        if (msg != null) {
                            transferFile.cloudMessageDeal(msg);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }
}
