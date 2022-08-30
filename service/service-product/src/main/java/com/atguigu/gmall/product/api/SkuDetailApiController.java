package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-26-21:35
 */
//商品详情数据库层操作
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {
    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    BaseCategory3Service baseCategory3Service;
    /**
     * 数据库层真正查询商品详情
     * @param skuId
     * @return
     */
    // @GetMapping("/skudetail/{skuId}")
    // public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId")Long skuId){
    //     //准备查询所有需要的数据
    //     SkuDetailTo skuDetailTo = skuInfoService.getSkuDetail(skuId);
    //     return Result.ok(skuDetailTo);
    //
    // }

    /**
     * 查询sku的基本信息
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo>getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo =skuInfoService.getDetailInfo(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 查询某个sku的所有图片
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/images/{skuId}")
    public Result<List<SkuImage>>getSkuImages(@PathVariable("skuId") Long skuId){
        List<SkuImage> images=skuInfoService.getDetailImages(skuId);
        return Result.ok(images);
    }

    /**
     * 查询sku的实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSku1010price(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuInfoService.get1010Price(skuId);
        return Result.ok(price);
    }

    /**
     * 查询sku对应的spu定义的所有销售属性名和值，并标记出当前sku是哪个
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/saleattrvalues/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleattrvalues(@PathVariable("spuId")Long spuId,@PathVariable("skuId")Long skuId){
        List<SpuSaleAttr> saleAttrList=spuSaleAttrService
                .getSaleAttrAndValueMarkSku(spuId,skuId);
        return Result.ok(saleAttrList);
    }


    /**
     * 查询sku组合 valueJson
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId")Long spuId){
        String valueJson=spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        return  Result.ok(valueJson);
    }

    /**
     * 查询分类
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result getCategoryView(@PathVariable("c3Id")Long c3Id){
        CategoryViewTo categoryViewTo= baseCategory3Service.getCategoryView(c3Id);
        return Result.ok(categoryViewTo);
    }
}
