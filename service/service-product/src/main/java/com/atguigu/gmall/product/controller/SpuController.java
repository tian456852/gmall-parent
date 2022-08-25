package com.atguigu.gmall.product.controller;

/**
 * @author tkwrite
 * @create 2022-08-25-20:16
 */

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spu功能
 */

@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;
//    /admin/product/1/10?category3Id=3

    /**
     * 分页获取
     * @param pg
     * @param sz
     * @return
     */
    @GetMapping("/{pg}/{sz}")
    public Result getSpuPage(
            @PathVariable("pg")Long pg,
            @PathVariable("sz")Long sz,
            @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo>page=new Page<>(pg,sz);
        QueryWrapper<SpuInfo> wrapper=new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        //分页查询
        Page<SpuInfo> result = spuInfoService.page(page, wrapper);
        return Result.ok(result);
    }
    //SPU定义这种商品的所有销售属性（颜色【1,2,3】、版本【4，5,6】、套餐【7,8,9】）
    //SKU只是SPU当前定义的所有销售属性中的一个精确组合。

    //    http://192.168.6.1/admin/product/spuImageList/28

    /**
     * 查询这个spu所有的图片
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId")Long spuId){
        QueryWrapper<SpuImage> wrapper=new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }
}
