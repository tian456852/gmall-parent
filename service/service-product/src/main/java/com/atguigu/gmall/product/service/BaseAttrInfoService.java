package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author KunTian
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-08-23 10:16:44
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    /**
     *
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存平台属性
     * @param info
     */
    void saveAttrInfo(BaseAttrInfo info);
}
