package com.atguigu.gmall.model.vo.search;

import lombok.Data;

/**
 * @author tkwrite
 * @create 2022-09-05-17:14
 */
@Data
public class OrderMapVo {
    private String type; //排序类型， 1是综合，2是价格
    private String sort; //排序规则
}
