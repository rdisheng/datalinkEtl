package com.leon.datalink.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.UnknownHostException;

/**
 * @ClassName Datalink
 * @Description
 * @Author Leon
 * @Date2022/3/29 15:57
 * @Version V1.0
 **/
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.leon.datalink")
@ServletComponentScan
public class Datalink {
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(Datalink.class, args);
    }
}
