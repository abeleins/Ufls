package com.kedong.ieduflsreceive.configuration;

import CloudPlatform.CloudPlatformMsgProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息总线配置
 * 消息生产者初始化接口
 */
@Configuration
public class MsgBusProducerConfig {

    /**
     * 消息的发布（publish）称作producer
     * producer产生和推送(push)数据到broker（中间的存储阵列称作broker）
     * @return
     */
    @Bean
    public CloudPlatformMsgProducer msgProducer() {
        CloudPlatformMsgProducer producer = new CloudPlatformMsgProducer();
        producer.init();
        return producer;
    }
}
