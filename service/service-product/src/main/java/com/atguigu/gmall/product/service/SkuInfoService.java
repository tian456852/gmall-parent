package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     * 上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 获取sku商品详情数据
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);

    /**
     * 获取sku的实时价格
     * @param skuId
     * @return
     */
    BigDecimal get1010Price(Long skuId);

    /**
     * 查询sku的基本信息
     * @param skuId
     * @return
     */
    SkuInfo getDetailInfo(Long skuId);


    /**
     * 查询sku的所有图片
     * @param skuId
     * @return
     */
    List<SkuImage> getDetailImages(Long skuId);

    /**
     * 找到所有的商品id
     * @return
     */
    List<Long> findAllSkuId();
}
