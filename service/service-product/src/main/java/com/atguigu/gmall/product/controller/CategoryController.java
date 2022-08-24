package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-22-21:14
 */
/*
分类的请求处理器
前后分离：前端发请求，后台处理好后响应JSON数据
所有请求全部返回 Result 对象的JSON。所有要携带的数据放到Result的data属性内即可
 */
//@ResponseBody   所有的响应数据都直接写给浏览器（如果是对象写成json，如果是文本就写成普通字符串）
//@Controller 这个类是来接受请求的
@RestController
@RequestMapping("/admin/product") //抽取公共路径
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;
     @Autowired
    BaseCategory2Service baseCategory2Service;
    @Autowired
    BaseCategory3Service baseCategory3Service;

    /*
    获取所有的一级分类
    @GetMapping：GET请求
    @PostMapping：POST请求
     */
    // 192.168.6.66:7000/admin/product/getCategory1
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        // {code:2000,message:"ok",data: [{id:1},{id:2},{id:3}]}
        //利用MyBatisPlus提供好的CRUD方法，直接查出有一级分类
        List<BaseCategory1> list=baseCategory1Service.list();

        return Result.ok(list);
    }
    /**
     * 获取某个一级分类下的所有二级分类
     * @param c1Id 传入一个一级分类id
     */
    //http://192.168.6.1/admin/product/getCategory2/9
    @GetMapping("/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id")Long c1Id){
        //查询 父分类id是c1Id 的所有二级分类
        List<BaseCategory2> category2s = baseCategory2Service.getCategory1Child(c1Id);
        return Result.ok(category2s);
    }
    /**
     * 获取某个二级分类下的所有三级分类
     * @param c2Id 传入一个二级分类id
     */
    //http://192.168.6.1/admin/product/getCategory3/9
    @GetMapping("/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id")Long c2Id){
        //查询 父分类id是c1Id 的所有二级分类
        List<BaseCategory3> category3s = baseCategory3Service.getCategory2Child(c2Id);
        return Result.ok(category3s);
    }

}
