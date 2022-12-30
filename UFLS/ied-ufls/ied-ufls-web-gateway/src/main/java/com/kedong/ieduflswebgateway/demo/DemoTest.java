package com.kedong.ieduflswebgateway.demo;

import com.kedong.ieduflswebcommon.service.FaultService;
import dcloud.common.InnerServiceBus.ServiceBus;

public class DemoTest {
    public static void main(String[] args) {
        //新建本地服务总线实例
        ServiceBus servBus = new ServiceBus();
        //初始化本地服务总线
        int init = servBus.init();
        String s = servBus.appid;
        //若要循环调用方法，则循环调用locateService方法
        FaultService l = servBus.locateService("com.kedong.ieduflswebcommon.service.FaultService");
        //调用服务实例的sayHello()方法
        System.out.println("==================" + l.toString());
    }
}
