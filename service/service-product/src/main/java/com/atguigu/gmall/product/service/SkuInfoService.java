package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author KunTian
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 10:16:44
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * sku基本信息 sku_info
     * sku图片信息 sku_image
     * sku平台属性名和值
     * sku销售属性名和值
     * @param info
     */
    void saveSkuInfo(SkuInfo info);

    void cancelSale(Long skuId);

    void onSale(Long skuId);
}
