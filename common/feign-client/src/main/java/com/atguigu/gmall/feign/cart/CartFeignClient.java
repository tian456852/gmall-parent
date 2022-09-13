package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author tkwrite
 * @create 2022-09-07-22:27
 *
 */
@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 删除购物车中选中商品
     * @return
     */
    @GetMapping("/deleteChecked")
    Result deleteChecked();

    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return 把哪个商品添加到了购物车
     */
    @GetMapping("/addToCart")
    Result<Object> addToCart(
            @RequestParam("skuId")Long skuId,
            @RequestParam("num")Integer num);

}
