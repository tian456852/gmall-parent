package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author tkwrite
 * @create 2022-08-26-21:00
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
    /**
     * 可配置的线程池，可自动注入
     */
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
        // 准备查询所有需要的数据
        // Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);

        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //1、查基本信息
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);
        //2、查商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            List<SkuImage> images = skuImages.getData();
            skuInfo.setSkuImageList(images);
        }, executor);


        //3、查商品实时价格
        CompletableFuture<Void> sku1010Price = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> sku1010price = skuDetailFeignClient.getSku1010price(skuId);
            BigDecimal priceData = sku1010price.getData();
            detailTo.setPrice(priceData);
        }, executor);

        //4、查销售属性名值
        CompletableFuture<Void> skuSaleattrvaluesFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> skuSaleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(spuId, skuId);
            detailTo.setSpuSaleAttrList(skuSaleattrvalues.getData());
        }, executor);



        //5、查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long spuId = skuInfo.getSpuId();
            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(spuId);
            detailTo.setValuesSkuJson(skuValueJson.getData());
        }, executor);

        //6、查分类
        CompletableFuture<Void> categoryViewFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long c3Id = skuInfo.getCategory3Id();
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(c3Id);
            detailTo.setCategoryView(categoryView.getData());
        }, executor);
        CompletableFuture.allOf(imageFuture,sku1010Price,skuSaleattrvaluesFuture,skuValueFuture,categoryViewFuture)
                .join();
        return detailTo;
    }
}
