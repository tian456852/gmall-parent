package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.bloom.impl.SkuBloomDataQueryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tkwrite
 * @create 2022-09-01-18:39
 */
@RequestMapping("/admin/product")
@RestController
public class BloomOpsController {
    @Autowired
    BloomOpsService bloomOpsService;

    @Autowired
    BloomDataQueryService bloomDataQueryService;

    @GetMapping("/rebuild/sku/now")
    public Result rebuildBloom(){
        String bloomName= SysRedisConst.BLOOM_SKUID;
        bloomOpsService.rebuildBloom(bloomName,bloomDataQueryService);
        return Result.ok();
    }

}
