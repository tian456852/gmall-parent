package com.atguigu.gmall.item;

// import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.config.threadpool.AppThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author tkwrite
 * @create 2022-08-26-20:23
 */
/**
 * 1、公共的配置搬家放到 service-util
 * 2、当前项目依赖了 service-util
 *
 * 当前应用启动只会扫描 ItemMainApplication 所在包的所有组件
 * - com.atguigu.gmall.item.*****
 * - com.atguigu.gmall.common.**
 */
/**
 * 1、RedisAutoConfiguration
 *    给容器中放了 RedisTemplate<Object, Object> 和 StringRedisTemplate
 *    给redis存数据，都是k-v（v有很多类型）【string,jsonstring】
 *    StringRedisTemplate = RedisTemplate<String, String> ；
 *    给redis存数据，key是string，value序列化成字符串
 */
// @EnableAspectJAutoProxy //开启aspect的自动代理功能
// @Import(RedissonAutoConfiguration.class)
@EnableThreadPool
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.search"
})
@SpringCloudApplication
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }

}
