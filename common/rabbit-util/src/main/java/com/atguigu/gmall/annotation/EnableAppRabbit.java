package com.atguigu.gmall.annotation;

import com.atguigu.gmall.rabbit.AppRabbitConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @author tkwrite
 * @create 2022-09-14-20:04
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(AppRabbitConfiguration.class)
public @interface EnableAppRabbit {

}
