package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-08-26-19:57
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
//告诉springboot 这是一个远程调用的客户端,调用service-product微服务的功能
//远程调用之前feign会自己找nacos要到 service-product 真正的地址

//service-product ：当前客户端的名字，也是这个feign要发起远程调用时找的微服务的名字


public interface CategoryFeignClient {

    /**
     *1、 给 service-product 发送一个 GET方式的请求 路径是 /api/inner/rpc/product/category/tree
     *2、 拿到远程的响应 json 结果后转成 Result类型的对象，并且 返回的数据是 List<CategoryTreeTo>
     *
     * @return
     */
    @GetMapping("/category/tree")
    public Result<List<CategoryTreeTo>> getCategoryTree();


}
