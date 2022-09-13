package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @author tkwrite
 * @create 2022-09-05-14:45
 */

public interface GoodsService {


    void saveGoods(Goods goods);

    void deleteGoods(Long skuId);

    SearchResponseVo search(SearchParamVo searchParamVo);

    void updateHotScore(Long skuId, Long score);
}
