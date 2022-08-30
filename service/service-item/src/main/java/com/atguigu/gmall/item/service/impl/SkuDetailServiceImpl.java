package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tkwrite
 * @create 2022-08-26-21:00
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 可配置的线程池，可自动注入
     */
    @Autowired
    ThreadPoolExecutor executor;

    //未缓存优化前

    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {

        SkuDetailTo detailTo = new SkuDetailTo();
        // 准备查询所有需要的数据
        // Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);

        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //1、查基本信息
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);
        //2、查商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            List<SkuImage> images = skuImages.getData();
            skuInfo.setSkuImageList(images);
        }, executor);


        //3、查商品实时价格
        CompletableFuture<Void> sku1010Price = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> sku1010price = skuDetailFeignClient.getSku1010price(skuId);
            BigDecimal priceData = sku1010price.getData();
            detailTo.setPrice(priceData);
        }, executor);

        //4、查销售属性名值
        CompletableFuture<Void> skuSaleattrvaluesFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> skuSaleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(spuId, skuId);
            detailTo.setSpuSaleAttrList(skuSaleattrvalues.getData());
        }, executor);



        //5、查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long spuId = skuInfo.getSpuId();
            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(spuId);
            detailTo.setValuesSkuJson(skuValueJson.getData());
        }, executor);

        //6、查分类
        CompletableFuture<Void> categoryViewFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            Long c3Id = skuInfo.getCategory3Id();
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(c3Id);
            detailTo.setCategoryView(categoryView.getData());
        }, executor);
        CompletableFuture.allOf(imageFuture,sku1010Price,skuSaleattrvaluesFuture,skuValueFuture,categoryViewFuture)
                .join();
        return detailTo;
    }


    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //1.看缓存中有没有  sku:info:50
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if (StringUtils.isEmpty(jsonStr)){
        //    2.redis没有缓存数据
        //      2.1回源
            SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
            //  2.2放入缓存[把查到的对象转为json字符串保存到redis]
            redisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toStr(fromRpc));
            return fromRpc;
        }
        //3.缓存中有,把json转为指定的对象
        SkuDetailTo skuDetailTo=Jsons.toObj(jsonStr,SkuDetailTo.class);
        return skuDetailTo;
    }


//     //500w  100w：49  100w：50  100w：51   100w: 52    100w: 53
//     public SkuDetailTo getSkuDetailXxxxFeature(Long skuId) {
//         lockPool.put(skuId,new ReentrantLock());
//         //每个不同的sku，用自己专用的锁
//         //1、看缓存中有没有  sku:info:50
//         String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
//         if ("x".equals(jsonStr)) {
//             //说明以前查过，只不过数据库没有此记录，为了避免再次回源，缓存了一个占位符
//             return null;
//         }
//         //
//         if (StringUtils.isEmpty(jsonStr)) {
//             //2、redis没有缓存数据
//             //2.1、回源。之前可以判断redis中保存的sku的id集合，有没有这个id
//             //防止随机值穿透攻击？ 回源之前，先要用布隆/bitmap判断有没有
// //            int result = getbit(49);
//             // TODO 加锁解决击穿
//             SkuDetailTo fromRpc = null;
//
//             //JVM 抢不到锁的等1s。 怎么判断synchronized 抢到还是没抢到？
//
// //            ReentrantLock lock = new ReentrantLock();  //锁不住
//
// //            lock.lock(); //等锁，必须等到锁
//             //判断锁池中是否有自己的锁
//             //锁池中不存在就放一把新的锁，作为自己的锁，存在就用之前的锁
//             ReentrantLock lock = lockPool.putIfAbsent(skuId, new ReentrantLock());
//
//             boolean b = this.lock.tryLock(); //立即尝试加锁，不用等，瞬发。等待逻辑在业务上 .抢一下，不成就不用再抢了
// //            boolean b = lock.tryLock(1, TimeUnit.SECONDS); //等待逻辑在锁上.1s内，CPU疯狂抢锁
//             if(b){
//                 //抢到锁
//                 fromRpc = getSkuDetailFromRpc(skuId);
//             }else {
//                 //没抢到
// //                Thread.sleep(1000);
//                 jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
//                 //逆转为 SkuDetailTo
//                 return null;
//             }
//
//
//
//
//             //2.2、放入缓存【查到的对象转为json字符串保存到redis】
//             String cacheJson = "x";
//             if (fromRpc != null) {
//                 cacheJson = Jsons.toStr(fromRpc);
//                 //加入雪崩解决方案。固定业务时间+随机过期时间
//                 redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
//             } else {
//                 redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);
//             }
//
//             return fromRpc;
//         }
//         //3、缓存中有. 把json转成指定的对象
//         SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
//         return skuDetailTo;
//     }


//    @Override  //使用本地缓存
//    public SkuDetailTo getSkuDetail(Long skuId) {
//
//        //1、先看缓存
//        SkuDetailTo cacheData = skuCache.get(skuId);
//        //2、判断
//        if(cacheData == null){
//            //3、缓存没有，真正查询【回源（回到数据源头真正检索）】【提高缓存的命中率】
//            // 1 - 0/1： 0%
//            // 2 - 1/2: 50%
//            // N - (N-1)/N： 无限接近100%
//            //缓存命中率提升到100%；预缓存机制；
//            SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
//            skuCache.put(skuId,fromRpc);
//            return fromRpc;
//        }
//        //4、缓存有
//        return cacheData;
//    }
}
