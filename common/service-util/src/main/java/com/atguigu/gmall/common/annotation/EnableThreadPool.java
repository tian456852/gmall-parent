package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import com.atguigu.gmall.common.config.threadpool.AppThreadPoolProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tkwrite
 * @create 2022-08-28-15:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(AppThreadPoolAutoConfiguration.class)
//1.导入AppThreadPoolAutoConfiguration 组件
//2.开启@EnableConfigurationProperties(AppThreadPoolProperties.class)
//        -和配置文件绑定
//        -AppThreadPoolProperties放到容器
//    3.AppThreadPoolAutoConfiguration 给容器中放入ThreadPoolExecutor
//    效果：随时 @Autowired ThreadPoolExecutor即可，也很方便改配置
public @interface EnableThreadPool {

}
