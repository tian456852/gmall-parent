package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author tkwrite
 * @create 2022-08-24-11:32
 */
@RequestMapping("/admin/product")
@RestController
public class FileuploadController {
    /**
     * 文件上传功能
     * 1.前端把文件流放到哪里了？该怎么拿
     *      post请求数据在请求体（包含了文件[流]）
     * 如何接    (MultipartFile多部件上传)
     *      @RequestParam("file")MultipartFile file
     *      @RequestPart("file")MultipartFile file  专门处理文件
     *
     *各种注解接不通位置的请求数据
     * @RequestParam: 无论是什么请求 接请求参数； 用一个Pojo把所有数据都接了
     * @RequestPart： 接请求参数里面的文件项
     * @RequestBody： 接请求体中的所有数据 (json转为pojo)
     * @PathVariable: 接路径上的动态变量
     * @RequestHeader: 获取浏览器发送的请求的请求头中的某些值
     * @CookieValue： 获取浏览器发送的请求的Cookie值
     * - 如果多个就写数据，否则就写单个对象
     *
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file")MultipartFile file){

        return Result.ok();
    }

}
