package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-01-18:46
 */
@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;

    /**
     *
     * @param bloomName
     */
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService dataQueryService) {
        RBloomFilter<Object> oldbloomFilter = redissonClient.getBloomFilter(bloomName);

        //    1.准备一个新的布隆过滤器。所有东西都要准备好
        String newBloomName=bloomName+"_new";
        RBloomFilter<Object> bloomFilter =redissonClient.getBloomFilter(newBloomName);
    //    2.拿到所有的商品id
    //     List<Long> allSkuId = skuInfoService.findAllSkuId();
        List list = dataQueryService.queryData();
        //    3.初始化新的布隆
        bloomFilter.tryInit(5000000,0.00001);
        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }
    //    4.新布隆准备就绪
    //     冒泡交换过滤器名字


    //    5.两个交换： 大数据量的删除会导致redis卡死
    //    更极致做法：lua脚本
        oldbloomFilter.rename("bbb_bloom");//老布隆下线
        bloomFilter.rename(bloomName);//新布隆上线

    //    6.删除老布隆和中间的交换层
        oldbloomFilter.deleteAsync();
        redissonClient.getBloomFilter("bbb_bloom").deleteAsync();

    }
}
