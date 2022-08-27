package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * DDD(Data Display Debugger) 领域驱动设计
 *
 * 三级分类树形结构
 * @author tkwrite
 * @create 2022-08-26-19:11
 */
//支持无限层级
//   当前只有三级

@Data
public class CategoryTreeTo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild; //子分类

}
