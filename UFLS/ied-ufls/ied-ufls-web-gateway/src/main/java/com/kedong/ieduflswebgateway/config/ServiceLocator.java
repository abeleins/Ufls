package com.kedong.ieduflswebgateway.config;

import com.kedong.ieduflswebcommon.service.FaultService;
import dcloud.common.InnerServiceBus.ServiceBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceLocator {
    @Autowired
    private ServiceBus servBus;

    /**
     * 定位服务位置
     * @return
     */

    public FaultService faultService() {
        return servBus.locateService("com.kedong.ieduflswebcommon.service.FaultService");
    }
}
