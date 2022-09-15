package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-07-21:26
 */
@Slf4j
@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartApiController {
    @Autowired
    CartService cartService;

    /**
     * 删除购物车中选中商品
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteCartChecked(cartKey);
    return Result.ok();
    }
    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return 把哪个商品添加到了购物车
     */
    @GetMapping("/addToCart")
    public Result<SkuInfo> addToCart(
            @RequestParam("skuId")Long skuId,
            @RequestParam("num")Integer num){

        //老请求  RequestContextHolder 绑定当前线程

        SkuInfo skuInfo=cartService.addToCart(skuId,num);

        // UserAuthInfo authInfo= AuthUtils.getCurrentAuthInfo();
        // // System.out.println("service-cart 获取到的用户id："+userId);
        // log.info("用户id：{}，临时id：{}",authInfo.getUserId(),authInfo.getUserTempId());
        return Result.ok(skuInfo);

    }

    /**
     * 获取购物车选中的所有商品
     * @return
     */
    @GetMapping("/checked/list")
    public Result<List<CartInfo>>getChecked(){

        String cartKey = cartService.determinCartKey();
        List<CartInfo> checkItems=cartService.getCheckItems(cartKey);
        return Result.ok(checkItems);

    }

}
