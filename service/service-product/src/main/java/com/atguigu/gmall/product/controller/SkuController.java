package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-25-21:53
 */
@RequestMapping("/admin/product")
@RestController
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

//    /admin/product/list/1/10
    @GetMapping("/list/{page}/{size}")
    public Result getSkuList(
            @PathVariable("page")Long page,
            @PathVariable("size")Long size){
        Page<SkuInfo> page1=new Page<>(page,size);

        Page<SkuInfo> result = skuInfoService.page(page1);
        return  Result.ok(result);
    }

//    /admin/product/saveSkuInfo

    /**
     * 接前端的json数据，可以使用逆向生成vo【和前端对接的Javabean对象】
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo info){
        //sku大保存
        skuInfoService.saveSkuInfo(info);
        return Result.ok();
}

//http://192.168.6.1/admin/product/cancelSale/40

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId")Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }
    ///192.168.6.1/admin/product/onSale/49

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 修改SKu信息
     */
    public void updateSkuInfo(SkuInfo skuInfo){
       // skuInfoService.updateSkuInfo(skuInfo);
       // cacheOpsService.delay2Delete(skuInfo.getId());
    }




}
