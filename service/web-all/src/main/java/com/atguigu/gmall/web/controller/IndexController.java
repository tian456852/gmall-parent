package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.model.to.CategoryTreeTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-26-18:56
 */
@Controller
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;

    @GetMapping({"/","/index"})
    public String indexPage(Model model){

        // TODO 远程调用 查询出所有菜单 封装成一个树形结构的模型
        Result<List<CategoryTreeTo>> result = categoryFeignClient.getCategoryTree();
        if (result.isOk()){
            //远程调用成功
            List<CategoryTreeTo> data = result.getData();
            model.addAttribute("list",data);
        }
        //classpath:/templates/+index/index+.html
        return "index/index";//页面逻辑视图
    }

}
