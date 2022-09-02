package com.atguigu.starter.cache;

import com.atguigu.starter.cache.aspect.CacheAspect;
import com.atguigu.starter.cache.service.CacheOpService;
import com.atguigu.starter.cache.service.impl.CacheOpServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author tkwrite
 * @create 2022-09-02-19:56
 */

/**
 * 以前容器中的所有组件要导入进去
 * 整个缓存场景涉及到的所有组件都得注入到容器
 */
// @Import(CacheAspect.class)
@EnableAspectJAutoProxy
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class MallCacheAutoConfiguration {
    @Autowired
    RedisProperties redisProperties;

    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }

    @Bean
    public CacheOpService cacheOpService(){
        return new CacheOpServiceImpl();
    }

    @Bean
    public RedissonClient redissonClient(){
        //1、创建一个配置
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();
        //2、指定好redisson的配置项
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
                .setPassword(password);

        //3、创建一个 RedissonClient
        RedissonClient client = Redisson.create(config);
        //Redis url should start with redis:// or rediss:// (for SSL connection)


        return client;
    }

}
