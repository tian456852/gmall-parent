package com.atguigu.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author tkwrite
 * @create 2022-09-03-20:16
 */
@EnableElasticsearchRepositories //开启ES的自动仓库功能 写Bean,写接口，自动创好索引库并设置好Mapping类型
@SpringCloudApplication
public class SearchMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchMainApplication.class,args);
    }

}
