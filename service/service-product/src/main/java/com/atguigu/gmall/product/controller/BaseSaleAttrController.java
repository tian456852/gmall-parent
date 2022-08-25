package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-25-20:54
 */
@RequestMapping("/admin/product")
@RestController
public class BaseSaleAttrController {
    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    //    /admin/product/baseSaleAttrList

    /**
     * 获取所有销售属性的名字
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }
//    /admin/product/saveSpuInfo
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo info){
        spuInfoService.saveSpuInfo(info);
        return Result.ok();
    }

//    /admin/product/spuSaleAttrList/28

    /**
     * 查询出指定spu当时定义的所有销售属性的名和值
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId")Long spuId){
        List<SpuSaleAttr> spuSaleAttrs=spuSaleAttrService.getSaleAttrAndValueBySpuId(spuId);
        return Result.ok(spuSaleAttrs);
    }

}
