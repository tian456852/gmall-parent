package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author tkwrite
 * @create 2022-08-22-19:18
 */
/*
主启动类
 */
// @SpringBootApplication
// @EnableDiscoveryClient //开启服务发现[1、导入服务发现jar 2、使用这个注解]
// @EnableCircuitBreaker //开启熔断降级、流量保护 [1、导入jar 2、使用这个注解]
// @SpringCloudApplication    //以上的合体
// public class GatewayMainApplication {
//     public static void main(String[] args) {
//         SpringApplication.run(GatewayMainApplication.class,args);
//     }
//
// }
@SpringCloudApplication  //以上的合体
public class GatewayMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}
