package com.atguigu.gmall.cart.service.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.web.SkipPatternProvider;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author tkwrite
 * @create 2022-09-08-20:16
 */
@Service

public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SkuDetailFeignClient skuFeignClient;

    @Autowired
    ThreadPoolExecutor executor;
    @Override
    public SkuInfo addToCart(Long skuId, Integer num) {
        //添加商品
        //cart:user:1==hash(skuId,skuInfo)
        //1.决定购物车选择哪个键（未登录/已登录）
        String cartKey=determinCartKey();
        //2.给购物车添加指定商品
        SkuInfo skuInfo=addItemToCat(skuId,num,cartKey);

    //    3.购物车超时设置。自动延期
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        if (authInfo.getUserId()==null){
        //    用户未登录状态一直操作临时购物车
            String tempKey = SysRedisConst.CART_KEY + authInfo.getUserTempId();
            //临时购物车有过期时间 自动延期
            redisTemplate.expire(tempKey, 90, TimeUnit.DAYS);
        }
        return skuInfo;

    }
    @Override
    public SkuInfo addItemToCat(Long skuId, Integer num, String cartKey) {
        // key(cartKey) - hash(skuId- skuInfo)
        //拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        Boolean hasKey = cart.hasKey(skuId.toString());
        //获取当前购物车的品类数量
        Long itemsSize = cart.size();
        //1.如果这个skuId之前没有添加过，就新增。还需要远程调用查询当前信息
        if (!hasKey){
            if (itemsSize+1>SysRedisConst.CART_ITEMS_LIMIT){
            //    异常机制
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }
        //    1.1远程获取商品信息
            SkuInfo data = skuFeignClient.getSkuInfo(skuId).getData();
            //1.2转为购物车中要保存的数据模型
            CartInfo item =converSkuInfo2CartInfo(data);
            //TODO 老师在 converSkuInfo2CartInfo中设置的 setSkuId
            item.setSkuId(skuId);
            item.setSkuNum(num);
            // Result<BigDecimal> sku1010price = skuFeignClient.getSku1010price(skuId);
            // item.setSkuPrice(sku1010price.getData()); //实时价格
            //    1.3给redis保存起来
            cart.put(skuId.toString(), Jsons.toStr(item));
            return data;
        }else {
        //2.如果这个skuId之前添加过，就修改skuId对应的商品的数量
        //    2.1获取实时价格
            Result<BigDecimal> sku1010price = skuFeignClient.getSku1010price(skuId);
            BigDecimal price = sku1010price.getData();//实时价格
        //    2.2获取商品原信息
            CartInfo cartInfo=getItemFromCart(cartKey,skuId);
        //    2.3更新商品
            cartInfo.setSkuPrice(price);
            cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
            cartInfo.setUpdateTime(new Date());
        //    2.4存入redis
            cart.put(skuId.toString(),Jsons.toStr(cartInfo));
            SkuInfo skuInfo=converCartInfo2SkuInfo(cartInfo);
            return skuInfo;

        }


    }

    private SkuInfo converCartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());

        return skuInfo;

    }

    @Override
    public CartInfo getItemFromCart(String cartKey, Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cartKey);
        //拿到购物车中指定商品的json数据
        String jsonData = ops.get(skuId.toString());
        return  Jsons.toObj(jsonData,CartInfo.class);

    }

    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        //流式编程
        List<CartInfo> infos = hashOps.values().stream()
                .map(str ->Jsons.toObj(str, CartInfo.class))
                .sorted((o2,o1) ->o1.getCreateTime().compareTo(o2.getCreateTime()))
                .collect(Collectors.toList());
        //顺便把购物车中所有商品的价格再次查询一遍进行更新  异步不保证立即执行，但保证执行
        //异步情况下拿不到老请求
        // 1.老请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //2.绑定请求到这个线程
        executor.submit(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);
            updateCartAllItemPrice(cartKey,infos);
        //    3.移除数据
            RequestContextHolder.resetRequestAttributes();
        });
        return infos;
    }

    @Override
    public void updateItemNum(Long skuId, Integer num, String cartKey) {
        //1.拿到hash操作购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        //2.拿到商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setSkuNum(item.getSkuNum()+num);
        item.setCreateTime(new Date());
    //    3.保存到购物车
        hashOps.put(skuId.toString(),Jsons.toStr(item));
    }

    @Override
    public void updateChecked(Long skuId, Integer status, String cartKey) {
        //1.拿到hash操作购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

    //    2.拿到要修改的商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setIsChecked(status);
        item.setUpdateTime(new Date());

    //    3.保存
        hashOps.put(skuId.toString(),Jsons.toStr(item));
    }

    @Override
    public void deleteItemCart(Long skuId, String cartKey) {
        //1.拿到hash操作购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
    //    2.删除商品
        hashOps.delete(skuId.toString());
    }

    @Override
    public void deleteCartChecked(String cartKey) {
        //1.拿到hash操作购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

    //    2.拿到选中的商品并删除  收集所有选中的商品id
        List<String> ids = getCheckItems(cartKey).stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());

        //3.删除选中商品
        if (ids!=null&&ids.size()>0){

        hashOps.delete(ids.toArray());
        }
    }

    @Override
    public List<CartInfo> getCheckItems(String cartKey) {
        //1.拿到hash操作购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //2.拿到购物车中所有商品
        List<CartInfo> cartList = getCartList(cartKey);
        List<CartInfo> checkItems = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .collect(Collectors.toList());
        return checkItems;
    }

    @Override
    public void mergeUserAndTempCart() {
    UserAuthInfo authInfo=AuthUtils.getCurrentAuthInfo();
        //1.判断是否需要合并
        if (authInfo.getUserId()!=null &&  !StringUtils.isEmpty(authInfo.getUserTempId())){
        //    2.可能需要合并

        //    3.临时购物车有商品，合并后删除临时购物车
            String tempCartKey=SysRedisConst.CART_KEY+authInfo.getUserTempId();
            //3.1获取临时购物车商品
            List<CartInfo> tempCartList = getCartList(tempCartKey);
            if (tempCartList!=null&&tempCartList.size()>0){
            //    临时购物车有数据，需要合并
                    String userCartKey = SysRedisConst.CART_KEY + authInfo.getUserId();
                for (CartInfo info : tempCartList) {
                    Long skuId = info.getSkuId();
                    Integer skuNum = info.getSkuNum();
                    addItemToCat(skuId,skuNum,userCartKey);
            //    3.2删除临时购物车
                redisTemplate.opsForHash().delete(tempCartKey,skuId.toString());
                }
            }


        }



        }

    @Override
    public void updateCartAllItemPrice(String cartKey,List<CartInfo>cartInfos) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        cartInfos.stream()
                .forEach(cartInfo ->{
            //    1.查出最新价格
            Result<BigDecimal> price = skuFeignClient.getSku1010price(cartInfo.getSkuId());
            //    2.设置新价格
            cartInfo.setSkuPrice(price.getData());
            cartInfo.setUpdateTime(new Date());
            hashOps.put(cartInfo.getSkuId().toString(),Jsons.toStr(cartInfo));

        });
    }


    /**
     * skuInfo转为CartInfo
     * @param data
     * @return
     */
    private CartInfo converSkuInfo2CartInfo(SkuInfo data) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setCartPrice(data.getPrice());
        cartInfo.setImgUrl(data.getSkuDefaultImg());
        cartInfo.setSkuName(data.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(data.getPrice());

        return cartInfo;
    }

    /**
     * 根据当前用户的登录信息，决定使用哪个购物车键（redis）
     * @return
     */
    @Override
    public String determinCartKey() {
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        String cartKey= SysRedisConst.CART_KEY;
        if (info.getUserId()!=null){
        //    说明用户已登录
            cartKey = cartKey + "" + info.getUserId();
        }else {
        //    用户未登录
            cartKey=cartKey+""+info.getUserTempId();
        }
        return cartKey;
    }
}
