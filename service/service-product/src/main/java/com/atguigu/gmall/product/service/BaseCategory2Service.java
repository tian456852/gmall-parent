package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author KunTian
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2022-08-22 22:11:09
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {

    /*
    查询一级分类下的所有二级分类
     */
    List<BaseCategory2> getCategory1Child(Long c1Id);

    /**
     * 查询所有分类以及它下面的子分类，并组装成树形结构
     * @return
     */
    List<CategoryTreeTo> getAllCategoryWithTree();
}
