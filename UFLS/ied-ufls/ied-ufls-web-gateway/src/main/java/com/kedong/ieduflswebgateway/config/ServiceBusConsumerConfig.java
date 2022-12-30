package com.kedong.ieduflswebgateway.config;

import dcloud.common.InnerServiceBus.ServiceBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBusConsumerConfig {
    //    服务总线实例
    ServiceBus servBus;

    public ServiceBusConsumerConfig() {
        //新建本地服务总线实例
        servBus = new ServiceBus();
        servBus.init();
    }

    @Bean
    public ServiceBus serviceBus() {
        return servBus;
    }

}
