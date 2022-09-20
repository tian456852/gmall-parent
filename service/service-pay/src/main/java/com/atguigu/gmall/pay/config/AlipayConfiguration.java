package com.atguigu.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tkwrite
 * @create 2022-09-16-20:19
 */
@Configuration
public class AlipayConfiguration {
    @Bean
    public AlipayClient alipayClient(AlipayProperties properties){
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                properties.getGatewayUrl(),
                properties.getAppId(),
                properties.getMerchantPrivateKey(),
                "json",
                properties.getCharset(),
                properties.getAlipayPublicKey(),
                properties.getSignType());

        return alipayClient;
    }

}
