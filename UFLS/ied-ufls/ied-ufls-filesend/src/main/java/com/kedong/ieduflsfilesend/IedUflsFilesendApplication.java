package com.kedong.ieduflsfilesend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@PropertySource(value = {"classpath:app.properties"})
public class IedUflsFilesendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IedUflsFilesendApplication.class, args);
    }

}
