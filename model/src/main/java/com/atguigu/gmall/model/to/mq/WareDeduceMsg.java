package com.atguigu.gmall.model.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-18-15:44
 */
@Data
public class WareDeduceMsg {
    Long orderId;
    String consignee;
    String consigneeTel;
    String orderComment;
    String orderBody;
    String deliveryAddress;
    String paymentWay = "2";
    List<WareDeduceSkuInfo> details;

}
