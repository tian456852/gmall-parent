package com.atguigu.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tkwrite
 * @create 2022-09-14-21:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderMsg {
    private Long orderId;
    private Long userId;
}
