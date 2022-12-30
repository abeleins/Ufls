package com.kedong.ieduflswebgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class IedUflsWebgateApplication {

    public static void main(String[] args) {
        SpringApplication.run(IedUflsWebgateApplication.class, args);
    }

}
