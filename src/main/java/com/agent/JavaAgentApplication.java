package com.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class JavaAgentApplication {

    public static void main(String[] args) {

        SpringApplication.run(JavaAgentApplication.class, args);
        System.out.println("项目启动成功！！！");
    }

}
