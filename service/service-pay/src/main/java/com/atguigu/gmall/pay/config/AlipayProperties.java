package com.atguigu.gmall.pay.config;

import com.alipay.api.AlipayClient;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author tkwrite
 * @create 2022-09-16-20:20
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {

    private String gatewayUrl;
    private String appId;
    private String merchantPrivateKey;
    private String charset;
    private String alipayPublicKey;
    private String signType;
    private String returnUrl;
    private String notifyUrl;





}
