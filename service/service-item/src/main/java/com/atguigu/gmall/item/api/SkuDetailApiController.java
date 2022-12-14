package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClien;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RelationSupport;

/**
 * @author tkwrite
 * @create 2022-08-26-20:43
 */
@RestController
@Api(tags = "三级分类的RPC接口")
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {
    @Autowired
    SkuDetailService skuDetailService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId")Long skuId){
    //    商品的详情
    SkuDetailTo skuDetailTo=skuDetailService.getSkuDetail(skuId);

        //更新热度分
        skuDetailService.updateHotScore(skuId);

        //TODO 远程调用商品服务查询
        return Result.ok(skuDetailTo);
    }

}
