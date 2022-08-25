package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author tkwrite
 * @create 2022-08-25-19:39
 */
@ConfigurationProperties(prefix = "app.minio")
//自动把配置文件中app.minio下配置的每个属性全都和这个JavaBean的每个属性一一对应
@Component
@Data
public class MinioProperties {

    String endpointUrl;

    String accessKey;

    String secreKey;

    String bucketName;

}
