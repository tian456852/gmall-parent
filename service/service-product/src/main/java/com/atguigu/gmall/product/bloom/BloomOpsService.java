package com.atguigu.gmall.product.bloom;

/**
 * @author tkwrite
 * @create 2022-09-01-18:41
 */
public interface BloomOpsService {
    /**
     * 重建指定布隆过滤器
     * @param bloomName
     */
    void rebuildBloom(String bloomName,BloomDataQueryService dataQueryService);
}
