package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-23-18:37
 */
/*
平台属性相关接口API
 */
@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;
//    /admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
//    /admin/product/attrInfoList/2/0/0
    /**
     * 查询某个分类下的所有平台属性
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id")Long category1Id,
                                  @PathVariable("category2Id")Long category2Id,
                                  @PathVariable("category3Id")Long category3Id
                                  ){
        List<BaseAttrInfo> infos=baseAttrInfoService.getAttrInfoAndValueByCategoryId(category1Id, category2Id, category3Id);
        return Result.ok(infos);
    }

//    /admin/product/saveAttrInfo
/**
 * 保存、修改属性信息---二合一
 * 前端把所有页面录入的数据以json的方式post传给我们
 * 请求体：
 *  {"id":null,"attrName":"出厂日期","category1Id":0,"category2Id":0,"category3Id":0,"attrValueList":[{"valueName":"2019","edit":false},{"valueName":"2020","edit":false},{"valueName":"2021","edit":false},{"valueName":"2022","edit":false}],"categoryId":2,"categoryLevel":1}
 *
 *  取出前端发送的请求的请求体中的数据 @RequestBody，
 *  并把这个数据(json)转成指定的BaseAttrInfo对象，
 *  BaseAttrInfo封装前端提交来的所有数据
 */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo info){

        baseAttrInfoService.saveAttrInfo(info);
        return Result.ok();
    }
// /admin/product/getAttrValueList/11

    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId")Long attrId){
        List<BaseAttrValue>values=baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(values);
    }
















}
