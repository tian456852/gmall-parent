package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author tkwrite
 * @create 2022-08-28-14:35
 */

/**
 * 配置线程池
 */
//1、AppThreadPoolProperties 里面的所有属性和指定配置绑定
//2、AppThreadPoolProperties 组件自动放到容器中
//开启自动化属性绑定配置
@EnableConfigurationProperties(AppThreadPoolProperties.class)
@Configuration
public class AppThreadPoolAutoConfiguration {
    @Autowired
    AppThreadPoolProperties appThreadPoolProperties;

    @Bean
    public ThreadPoolExecutor coreExecutor(){
        //int corePoolSize, 核心线程池： cpu核心数
        //int maximumPoolSize, 最大线程数
        //long keepAliveTime, 线程存活时间
        //TimeUnit unit, 时间
        //BlockingQueue<Runnable> workQueue,  阻塞队列 ，大小需要合理
        //ThreadFactory threadFactory, 线程工厂
        //RejectedExecutionHandler handler 拒绝策略
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                appThreadPoolProperties.getCore(),
                appThreadPoolProperties.getMax(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(appThreadPoolProperties.getQueueSize()),
                //队列大小由项目最终能占的最大内存决定
                new ThreadFactory() {//负责给线程池创建线程
                    int i=0;//记录线程自增id
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread=new Thread(r);
                        thread.setName("core-thread"+ i++);
                        return thread;
                    }
                },
                // 生产环境用 CallerRuns 保证就算线程池满了
                // 不能提交的任务，由当前线程自己以同步的方式执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // new ArrayBlockingQueue<>() 底层队列是一个数组
        // new LinkedBlockingDeque<>() 底层是一个链表
        //数组与链表？-- 检索、插入
        //数组是连续空间  链表不连续（利用碎片化空间）
        return executor;
    }

}
