package com.atguigu.gmall.search;

import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author tkwrite
 * @create 2022-09-05-19:25
 */
@SpringBootTest
public class SearchTest {
    @Autowired
    GoodsService goodsService;

    @Test
    void testSearch(){
        SearchParamVo searchParamVo = new SearchParamVo();
        searchParamVo.setCategory3Id(61L);
        goodsService.search(searchParamVo);
    }



}
