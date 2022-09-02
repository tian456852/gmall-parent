package com.atguigu.gmall.product;

import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author tkwrite
 * @create 2022-08-22-21:04
 * 1、SpringBoot如何抽取了Redis的配置。
 *   1)、写一些自动配置类，RedisAutoConfiguration，把所有未来用的组件都用这个配置类自动放到容器中
 *   2)、写一个专门用来绑定配置文件中配置的属性类  MinioProperties
 *
 * 默认只扫描 主程序所在的包和子包
 *  //主程序： com.atguigu.gmall.product
 *  //其他：   com.atguigu.gmall.common.config
 *  1、批量导入：@ComponentScan("com.atguigu.gmall.common.config")
 *  2、批量导入： @SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
 *  3、精准导入：@Import({Swagger2Config.class})
 */
@EnableScheduling
// @EnableTransactionManagement //开启注解的事务
// @EnableThreadPool
@Import({Swagger2Config.class})
@SpringCloudApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
