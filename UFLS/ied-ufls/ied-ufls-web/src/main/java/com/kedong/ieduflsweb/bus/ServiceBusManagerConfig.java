package com.kedong.ieduflsweb.bus;

import com.kedong.ieduflswebcommon.service.*;
import dcloud.common.InnerServiceBus.ServiceBus;
import dcloud.common.api.Service;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Log4j2
public class ServiceBusManagerConfig {
    //    区域名
    @Value("${service.bus.region}")
    private String region;
    //    应用名
    @Value("${service.bus.application}")
    private String application;
    //    公司编码
    @Value("${service.bus.vendor}")
    private String vendor;
    //    版本号
    @Value("${service.bus.version}")
    private String version;
    //    提供服务接口所在包名
    @Value("${service.bus.interface.pack}")
    private String interfacePack;
    //    提供服务接口实现类包名
    @Value("${service.bus.impl.pack}")
    private String implPack;
    //    提供服务接口名
    @Value("${service.bus.interface}")
    private String[] inArr;
    //    提供服务接口实现类名
    @Value("${service.bus.impl}")
    private String[] implArr;
    //    提供服务端口号
    @Value("${service.bus.port}")
    private String[] portArr;


    @Autowired
    private FaultService faultService;

    ServiceBus providerServiceBus;

    @PostConstruct
    /**
    *被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。、
    * PostConstruct在构造函数之后执行，init（）方法之前执行
    * @PostConstruct注解的方法将会在依赖注入完成后被自动调用
    * PreDestroy（）方法在destroy（）方法知性之后执行
    * */
    public ServiceBus serviceBus() {
        //新建服务总线实例
         providerServiceBus = new ServiceBus();
        //初始化本地服务总线
        providerServiceBus.init();
        //发布服务
        publishingServer(providerServiceBus);
        return providerServiceBus;
    }

    private void publishingServer(ServiceBus serviceBus) {
        if (inArr.length != implArr.length || inArr.length != portArr.length)
            throw new RuntimeException("服务总线初始化出错，请检查提供接口、实现类和端口号数量是否对应一致");

        //在循环中发布配置好的服务
        for (int i = 0; i < inArr.length; i++) {
            try {
                /**
                 *新建一个服务实例,第一个参数为区域名，第二个参数为应用名，第三个参数为公司编码，
                 * 第四个参数为版本号，第五个参数为服务名即接口类名，
                 * 第六个参数为接*口类的全限定名即PackageName.InterfaceName，第七个参数为接口实现类的全 *限定名。
                 **/
                String sInterFaceName = inArr[i];
                String sInterFacePacket = interfacePack + inArr[i];
                String sInterImplName = implPack + implArr[i];
                Service service_D = new Service(region, application, vendor, version,
                        sInterFaceName, sInterFacePacket, sInterImplName);
                //设置服务超时时间
                service_D.setTimeout(10000);
                //设置服务端口号
                service_D.setPort(Integer.parseInt(portArr[i]));

                service_D.setImplObject(getServiceByName(inArr[i]));
                //注册服务
                serviceBus.registerService(service_D);

                //发布服务
                boolean b = serviceBus.startService(service_D.getServiceID());
                log.info(b ? "服务发布成功，服务名称：" + interfacePack + inArr[i] + "，实现类：" + implPack + implArr[i] : "服务发布失败，请查看报错信息。服务名称：" + interfacePack + inArr[i] + "，实现类：" + implPack + implArr[i]);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("注册服务时出错，服务名称：" + inArr[i]);
            }
        }
    }

    public Object getServiceByName(String serviceName) {
        switch (serviceName) {
//            case "AppDataService":
//                return appDataService;
//            case "StatisticsService":
//                return statisticsService;
//            case "OnlineService":
//                return onlineService;
//            case "EquipmentService":
//                return equipmentService;
            case "FaultService":
                return faultService;
            default:
                throw new IllegalArgumentException("输入服务名无效或未被开放，请检查服务名称");
        }
    }

    @PreDestroy()
    public void destroyService(){
        boolean destroy = providerServiceBus.destroy();
        log.info(destroy?"所有服务销毁成功":"服务销毁失败，请检查并重试");
    }
}
