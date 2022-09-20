package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;

import com.atguigu.gmall.pay.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author tkwrite
 * @create 2022-09-16-18:48
 */
@Slf4j
@RequestMapping("/api/payment")
@Controller
public class PayController {
//http://api.gmall.com/api/payment/alipay/submit/777605982854316032

    @Autowired
    AlipayService alipayService;
    /**
     * 跳到支付宝收银台
     * @return
     */
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String aliPage(@PathVariable("orderId") Long orderId) throws AlipayApiException {

        String content= alipayService.getAlipayPageHtml(orderId);
        return content;

    }

    /**
     * 跳到支付成功页面
     * 支付成功以后，支付宝会命令浏览器 来到http://gmall.com/api/payment/paysuccess
     * @return
     */
    @GetMapping("/paysuccess")
    public String paysuccess(@RequestParam Map<String,String> paramMaps) throws AlipayApiException {
        System.out.println("支付成功同步通知页：收到的参数："+paramMaps);
        //1、如果要在这里改订单状态，先验签。验证是否支付宝给我们发来的数据
        boolean b =  alipayService.rsaCheckV1(paramMaps);
        if(b){
            //验签通过
            System.out.println("正在修改订单状态....订单信息："+paramMaps);
        }
        return "redirect:http://gmall.com/pay/success.html";
    }

    /**
     * 支付成功：异步通知
     */
    @ResponseBody
    @RequestMapping("/success/notify")
    public String notifySuccess(@RequestParam Map<String,String> param) throws AlipayApiException {

        boolean b = alipayService.rsaCheckV1(param);
        if(b){
            log.info("异步通知抵达。支付成功，验签通过。数据：{}", Jsons.toStr(param));
            //TODO 修改订单状态。用到支付最大努力通知
            alipayService.sendPayedMsg(param);
        }else {
            return "error";
        }
        return "success";
    }
}
