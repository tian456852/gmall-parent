package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-08-20:15
 */
public interface CartService {

    /**
     * 添加一个商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    SkuInfo addToCart(Long skuId, Integer num);

    /**
     * 根据当前用户的登录信息，决定使用哪个购物车键（redis）
     * @return
     */
     String determinCartKey();

    /**
     * 把指定商品添加至指定购物车
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
     SkuInfo addItemToCat(Long skuId,Integer num,String cartKey);

    /**
     * 从购物车中获取某个商品
     * @param cartKey
     * @param skuId
     * @return
     */
    CartInfo getItemFromCart(String cartKey, Long skuId);

    /**
     * 获取指定购物车中的所有商品。排序
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartList(String cartKey);

    /**
     * 更新购物车中某个商品数量
     * @param skuId
     * @param num
     * @param cartKey
     */
    void updateItemNum(Long skuId, Integer num, String cartKey);

    /**
     * 修改购物车中商品状态
     * @param skuId
     * @param status
     * @param cartKey
     */
    void updateChecked(Long skuId, Integer status, String cartKey);

    /**
     * 删除购物车商品
     * @param skuId
     * @param cartKey
     */
    void deleteItemCart(Long skuId, String cartKey);

    /**
     * 删除购物车选中商品
     * @param cartKey
     */
    void deleteCartChecked(String cartKey);

    /**
     * 获取指定购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getCheckItems(String cartKey);

    /**
     * 合并购物车
     */
    void mergeUserAndTempCart();


    /**
     * 更新这个购物车中所有商品的价格
     * @param cartKey
     * @param cartInfos 所有的商品
     */
   void updateCartAllItemPrice(String cartKey,List<CartInfo> cartInfos);
}
