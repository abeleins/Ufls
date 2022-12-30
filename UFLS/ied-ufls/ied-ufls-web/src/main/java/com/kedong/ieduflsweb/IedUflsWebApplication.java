package com.kedong.ieduflsweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@MapperScan(value = {"com.kedong.ieduflsweb.dao"})
@PropertySource(value = {"classpath:app.properties"})
public class IedUflsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IedUflsWebApplication.class, args);
    }

}
