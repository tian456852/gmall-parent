package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @author tkwrite
 * @create 2022-08-26-20:59
 */
public interface SkuDetailService {


    SkuDetailTo getSkuDetail(Long skuId);
}
