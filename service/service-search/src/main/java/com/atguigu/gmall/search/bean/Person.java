package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author tkwrite
 * @create 2022-09-03-20:26
 */
@Data
@Document(indexName = "person",shards = 1,replicas = 1)
public class Person {

    @Id
    private Long id; //主键
    @Field(value = "first",type = FieldType.Keyword) //TEXT【存的时候会分词】，keyword【关键字不分词】  两者都是字符串  TEXT,keyword:都是字符串
    private String firstName;
    @Field(value = "last",type = FieldType.Keyword)
    private String lastName;
    @Field(value = "age")
    private Integer age;
    @Field(value = "address",analyzer = "ik_smart")//自动决定，Java的string默认是ESTEXT类型
    private String address;
}
