package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author tkwrite
 * @create 2022-09-05-15:39
 */
@RequestMapping("/api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClien {

    @PostMapping("/goods")
     Result saveGoods(@RequestBody Goods goods);

    @DeleteMapping("/delete/{skuId}")
     Result deleteGoods(@PathVariable("skuId") Long skuId);

    @PostMapping("/goods/search")
     Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo);
}
