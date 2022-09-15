package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-13-21:17
 */
@Data
public class OrderSubmitVo {
    private String consignee;
    private String consigneeTel;
    private String deliveryAddress;
    private String paymentWay;
    private String orderComment;
    private List<CartInfoVo> orderDetailList;
}
