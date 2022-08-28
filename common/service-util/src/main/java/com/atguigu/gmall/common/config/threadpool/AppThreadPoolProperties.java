package com.atguigu.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tkwrite
 * @create 2022-08-28-14:58
 */
@Component
@Data
@ConfigurationProperties(prefix = "app.thread-pool")
public class AppThreadPoolProperties {
    Integer core = 2;
    Integer max = 4;
    Integer queueSize = 20;
    Long keepAliveTime = 300L;

}
