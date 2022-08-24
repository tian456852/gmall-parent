package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author KunTian
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-23 10:16:44
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> infos=  baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(category1Id,category2Id,category3Id);
        return infos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo info) {
        if (info.getId()==null){
    //    1.新增
            addBaseAttrInfo(info);
        }else{
        //  2. 修改
            updateBaseAttrInfo(info);
        }
    }

    private void updateBaseAttrInfo(BaseAttrInfo info) {
        // 2.1 修改属性名信息
        baseAttrInfoMapper.updateById(info);
        //    2.2修改属性值（有增有删有改）
        //   以前的值：2019、2020、2021、2022
        //    现在的值：2019以前、2021、2023以后
        //    ①以前记录全删，新提交数据全新增，导致引用失败
        //    ②正确做法：先删除前端提交里没有，数据库里有的
        //                如果前端提交里没有删除，则判断新增与修改

        List<BaseAttrValue> valueList = info.getAttrValueList();
        //删除
        //判断前端没提交的都是需要删除的
        //数据库：59，60.61.62
        //现在：59,61
        //前端没提交：60,62，说明这两个要删除[差集]

        //   1. 收集前端提交的所有属性值id
        List<Long>vids=new ArrayList<>();
        for (BaseAttrValue attrValue : valueList) {
            Long id = attrValue.getId();
            if (id!=null){
                vids.add(id);
            }
        }
        if (vids.size()>0){
        //    部分删除
        QueryWrapper<BaseAttrValue>deleteWrapper=new QueryWrapper<>();
        deleteWrapper.eq("attr_id", info.getId());
        deleteWrapper.notIn("id",vids);
        baseAttrValueMapper.delete(deleteWrapper);
        }else {
            //全删，前端一个属性值id都没带，把这个属性id下的所有属性值都删除
            QueryWrapper<BaseAttrValue>deleteWrapper=new QueryWrapper<>();
            deleteWrapper.eq("attr_id", info.getId());
            baseAttrValueMapper.delete(deleteWrapper);

        }

        for (BaseAttrValue attrValue : valueList) {
            if (attrValue.getId()!=null){
                //属性值有id，说明数据库以前有id，可能发生修改
                baseAttrValueMapper.updateById(attrValue);
            }if(attrValue.getId()==null){
            //    说明数据库以前没有， 新增
                attrValue.setAttrId(info.getId());
                baseAttrValueMapper.insert(attrValue);
            }


        }
    }

    private void addBaseAttrInfo(BaseAttrInfo info) {
        //    1.保存属性名
        baseAttrInfoMapper.insert(info);
        //    拿到刚才保存好的属性名的自增id
        Long id = info.getId();

        //    2.保存属性值
        List<BaseAttrValue> valueList= info.getAttrValueList();
        for (BaseAttrValue baseAttrValue : valueList) {
            //回填属性名记录的自增id
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }


}




