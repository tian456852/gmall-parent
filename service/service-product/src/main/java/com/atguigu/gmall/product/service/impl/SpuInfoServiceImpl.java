package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author KunTian
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2022-08-23 10:16:44
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{
    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo info) {
        //1.spu基本信息保存到spu_info
        spuInfoMapper.insert(info);
        Long spuId = info.getId();
        //    2.把spu的图片保存到spu_image
        List<SpuImage> imageList = info.getSpuImageList();
        for (SpuImage spuImage : imageList) {
            //回填spu_id
            spuImage.setSpuId(spuId);
        }
        spuImageService.saveBatch(imageList);

    //    3.保存销售属性名和值
    //    3.1保存销售属性名
        List<SpuSaleAttr> attrNameList = info.getSpuSaleAttrList();
        for (SpuSaleAttr attr : attrNameList) {
        //    回填spu_id
            attr.setSpuId(spuId);
//     3.2保存销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = attr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : spuSaleAttrValueList) {
            //    回填spu_id
                value.setSpuId(spuId);
                String saleAttrName = attr.getSaleAttrName();
                //回填 销售属性名
                value.setSaleAttrName(saleAttrName);
            }
            //    保存销售属性值
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        }
    //保存到数据库
        spuSaleAttrService.saveBatch(attrNameList);

    }

}




