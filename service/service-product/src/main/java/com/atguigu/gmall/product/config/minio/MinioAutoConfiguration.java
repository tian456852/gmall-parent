package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tkwrite
 * @create 2022-08-25-19:35
 */
@Configuration

public class MinioAutoConfiguration {

    @Autowired
    MinioProperties minioProperties;
    /**
     * 之后想进行上传的人，自动注入
     * @return
     */
    @Bean
    public MinioClient minioClient() throws Exception {
        //1.创建minio客户端
        MinioClient minioClient = new MinioClient(
                minioProperties.getEndpointUrl(),
                minioProperties.getAccessKey(),
                minioProperties.getSecreKey()
        );
        String bucketName = minioProperties.getBucketName();
        if(!minioClient.bucketExists(bucketName)){
            minioClient.makeBucket(bucketName);
        };

        return minioClient;
    }

}
