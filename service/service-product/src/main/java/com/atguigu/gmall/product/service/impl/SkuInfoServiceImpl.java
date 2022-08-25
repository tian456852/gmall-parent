package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author KunTian
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 10:16:44
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuImageService skuImageService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo info) {
    //    1.sku基本信息
        save(info);
        Long skuId = info.getId();
        //    2.sku图片信息sku_image
        for (SkuImage skuImage : info.getSkuImageList()) {
            skuImage.setSkuId(skuId);
        }
        skuImageService.saveBatch(info.getSkuImageList());
    //    3.sku的平台属性名和值的关系保存到sku_attr_value
        for (SkuAttrValue skuAttrValue : info.getSkuAttrValueList()) {
            skuAttrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(info.getSkuAttrValueList());
    //    4.sku的销售属性名和值的关系保存到sku_sale_attr_value
        for (SkuSaleAttrValue skuSaleAttrValue : info.getSkuSaleAttrValueList()) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(info.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(info.getSkuSaleAttrValueList());
    }

    @Override
    public void cancelSale(Long skuId) {
    //    1.改数据库 sku_info   1上架   0下架
        skuInfoMapper.updateIsSale(skuId,0);

    //    TODO 2.从es中删除商品
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,1);
        //    TODO 2.给es中保存商品
    }
}




