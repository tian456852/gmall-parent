package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsService;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tkwrite
 * @create 2022-09-05-14:42
 */
@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchApiController {
    @Autowired
    GoodsService goodsService;
    /**
     * 保存商品信息到es
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods){
        goodsService.saveGoods(goods);
        return Result.ok();
    }

    @DeleteMapping("/delete/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId){
        goodsService.deleteGoods(skuId);
        return Result.ok();
    }

    /**
     * 商品检索
     * @param searchParamVo
     * @return
     */
    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo){
        //TODO 检索
        SearchResponseVo responseVo=goodsService.search(searchParamVo);
        return Result.ok(responseVo);
    }

    /**
     * 增加热度分
     * @param skuId
     * @param score 商品最新得分
     * @return
     */
    @GetMapping("/goods/hotscore/{skuId}")
    public Result updateHotScore(@PathVariable("skuId")Long skuId,
                                 @RequestParam("score") Long score){
        goodsService.updateHotScore(skuId,score);
        return Result.ok();

    }
}
