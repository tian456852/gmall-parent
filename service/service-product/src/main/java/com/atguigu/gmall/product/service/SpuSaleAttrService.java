package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author KunTian
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2022-08-23 10:16:44
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     *根据spuId查询对应的所有销售属性名和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long spuId);

    /**
     * 查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId,Long skuId);
}
