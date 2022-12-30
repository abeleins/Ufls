package com.kedong.ieduflsfilesend.util;

import CloudPlatform.CloudPlatformMsgProducer;
import MsgAPI.CloudBusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Component
public class MessageTemplate {
    @Autowired
    private CloudPlatformMsgProducer msgProducer;

    public void send(String topic, String key, byte[] data) {
        CloudBusMessage message = new CloudBusMessage();
        message.setKey(key);
        message.setTopic(topic);
        message.setData(data);
        msgProducer.send(message);
    }

    public void send(String topic, byte[] data) {
        send(topic, null, data);
    }

    public void send(String topic, String key, String data) {
        send(topic, key, data.getBytes(StandardCharsets.UTF_8));
    }

    public void send(String topic, String data) {
        send(topic, null, data.getBytes(StandardCharsets.UTF_8));
    }
}
