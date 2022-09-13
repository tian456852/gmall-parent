package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.FeignInterceptorConfiguration;
import com.atguigu.gmall.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tkwrite
 * @create 2022-09-10-22:42
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignInterceptorConfiguration.class)
public @interface EnableAutoFeignInterceptor {
}
