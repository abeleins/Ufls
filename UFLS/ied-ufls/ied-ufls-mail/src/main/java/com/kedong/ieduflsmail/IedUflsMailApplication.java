package com.kedong.ieduflsmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:app.properties"})
public class IedUflsMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(IedUflsMailApplication.class, args);
    }

}
