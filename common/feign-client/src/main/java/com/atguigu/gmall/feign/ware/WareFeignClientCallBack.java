package com.atguigu.gmall.feign.ware;

import org.springframework.stereotype.Component;

/**
 * @author tkwrite
 * @create 2022-09-16-18:20
 */
@Component
public class WareFeignClientCallBack implements WareFeignClient{

    /**
     * 错误兜底
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public String hasStock(Long skuId, Integer num) {
        //统一显示有货
        return "1";
    }
}
