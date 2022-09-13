package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.redisson.RedissonSubList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-07-21:23
 */

/**
 * 购物车处理前端ajax请求
 */
@RestController
@RequestMapping("/api/cart")
public class CartRestController {

    @Autowired
    CartService cartService;




    /**
     * 删除购物车商品
     * @param skuId
     * @return
     */
    //http://api.gmall.com/api/cart/deleteCart/49
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable("skuId") Long skuId){
        String cartKey = cartService.determinCartKey();
        cartService.deleteItemCart(skuId,cartKey);
        return Result.ok();
    }

    /**
     * 修改勾选状态
     * @param skuId
     * @param status
     * @return
     */
    //http://api.gmall.com/api/cart/checkCart/49/0
    @GetMapping("/checkCart/{skuId}/{status}")
    public Result check(
            @PathVariable("skuId") Long skuId,
            @PathVariable("status") Integer status
    ){
        String cartKey = cartService.determinCartKey();
        cartService.updateChecked(skuId,status,cartKey);

        return Result.ok();


    }

    /**
     * 修改购物车中某个商品数量
     * @param skuId
     * @param num
     * @return
     */
    //http://api.gmall.com/api/cart/addToCart/50/1
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result updateItemNum(
            @PathVariable("skuId") Long skuId,
            @PathVariable("num") Integer num
    ){
        String cartKey = cartService.determinCartKey();
        cartService.updateItemNum(skuId,num,cartKey);
        return Result.ok();
    }

    @GetMapping("/cartList")
    public Result cartList(){
        //1.决定用哪个购物车键
        String cartKey = cartService.determinCartKey();

        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        cartService.mergeUserAndTempCart();

        //2.获取购物车商品
        List<CartInfo> infos=cartService.getCartList(cartKey);

        return Result.ok(infos);
    }

}
