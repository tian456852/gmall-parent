package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author tkwrite
 * @create 2022-08-26-9:15
 */
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product","com.atguigu.gmall.feign.item"})
 @SpringCloudApplication
//     @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//     @EnableDiscoveryClient
//     @EnableCircuitBreaker
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class,args);
    }
}
