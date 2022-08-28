package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

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

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
        //(√) 0、查询到skuInfo
        SkuInfo skuInfo=skuInfoMapper.selectById(skuId);
        //(√) 1、商品（sku）所属的完整分类信息：  base_category1、base_category2、base_category3
        CategoryViewTo categoryViewTo= baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        detailTo.setCategoryView(categoryViewTo);
        //(√) 2、商品（sku）的基本信息【价格、重量、名字...】   sku_info
        //把查询到的数据一定放到 SkuDetailTo 中
        detailTo.setSkuInfo(skuInfo);

        //(√) 实时价格查询
        BigDecimal price = get1010Price(skuId);
        detailTo.setPrice(price);

        //(√) 3、商品（sku）的图片        sku_image
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(imageList);

        //(√)4、商品（sku）所属的SPU当时定义的所有销售属性名值组合（固定好顺序）。
        //          spu_sale_attr、spu_sale_attr_value
        //          并标识出当前sku到底spu的那种组合，页面要有高亮框 sku_sale_attr_value
        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
        List<SpuSaleAttr> saleAttrList=spuSaleAttrService
                .getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        detailTo.setSpuSaleAttrList(saleAttrList);
        //5.商品（sku）的所有兄弟产品的销售属性名和值组合关系全部查出来并封装成
        //{"118|120":"50","110|121":"51"}这样的json组合
        Long spuId=skuInfo.getSpuId();
        String valueJson=spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        detailTo.setValuesSkuJson(valueJson);

        //以下省略不做
        //5、商品（sku）类似推荐    （x）
        //6、商品（sku）介绍[所属的spu的海报]        spu_poster（x）
        //7、商品（sku）的规格参数                  sku_attr_value
        //8、商品（sku）售后、评论...              相关的表 (x)

        return detailTo;
    }

    @Override
    public BigDecimal get1010Price(Long skuId) {
        BigDecimal price=skuInfoMapper.getRealPrice(skuId);
        return price;
    }

    @Override
    public SkuInfo getDetailInfo(Long skuId) {
        SkuInfo skuInfo=skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @Override
    public List<SkuImage> getDetailImages(Long skuId) {
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        return imageList;
    }


}




