package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-26-22:01
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeignClient {

    // @GetMapping("/skudetail/{skuId}")
    // Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku的基本信息
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo>getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 查询某个sku的所有图片
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/images/{skuId}")
    public Result<List<SkuImage>>getSkuImages(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku的实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSku1010price(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku对应的spu定义的所有销售属性名和值，并标记出当前sku是哪个
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/saleattrvalues/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleattrvalues(@PathVariable("spuId")Long spuId, @PathVariable("skuId")Long skuId);

    /**
     * 查询sku组合 valueJson
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId")Long spuId);

    /**
     * 查询分类
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result<CategoryViewTo> getCategoryView(@PathVariable("c3Id")Long c3Id);


}
