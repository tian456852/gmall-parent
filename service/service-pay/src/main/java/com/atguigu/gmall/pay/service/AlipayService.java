package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @author tkwrite
 * @create 2022-09-16-21:02
 */
public interface AlipayService {
    /**
     * 生成订单的支付页
     * @param orderId
     * @return
     */
    String getAlipayPageHtml(Long orderId) throws AlipayApiException;

    /**
     * 支付宝验签
     * @param paramMaps
     * @return
     */
    boolean rsaCheckV1(Map<String, String> paramMaps) throws AlipayApiException;

    /**
     * 发送支付成功消息给订单交换机
     * @param param
     */
    void sendPayedMsg(Map<String, String> param);
}
