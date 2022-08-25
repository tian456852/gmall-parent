package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-24-10:12
 */

/**
 * 品牌API
 */
@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

//    http://192.168.6.1/admin/product/baseTrademark/1/10
//                      /admin/product/baseTrademark/{page}/{limit}

    /**
     * 分页查询所有品牌
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page")Long page,
                                @PathVariable("limit")Long limit){

        //long current, long size
        Page<BaseTrademark> page1=new Page<>(page,limit);

        //pageResult 分页信息、查询到的记录集合
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page1);

        return Result.ok(pageResult);
    }
//      /admin/product/baseTrademark/save
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark trademark){
        baseTrademarkService.save(trademark);
        return Result.ok();
    }

//    http://api.gmall.com/admin/product/baseTrademark/remove/{id}
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id")Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();

    }



//    http://192.168.6.1/admin/product/baseTrademark/get/2
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id")Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

//    http://api.gmall.com/admin/product/baseTrademark/update
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark trademark){
        baseTrademarkService.updateById(trademark);
        return  Result.ok();
    }

//    /admin/product/baseTrademark/getTrademarkList
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }
}
