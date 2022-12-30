package com.kedong.ieduflsfilesend.configuration;

import CloudPlatform.CloudPlatformMsgProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBusConfig {

    /**
     *
     * @return
     */
    @Bean
    public CloudPlatformMsgProducer msgProducer() {
        CloudPlatformMsgProducer producer = new CloudPlatformMsgProducer();
        producer.init();
        return producer;
    }
}
