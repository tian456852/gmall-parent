package com.atguigu.gmall.model.to.mq;

import lombok.Data;

/**
 * 库存扣减状态
 */

@Data
public class WareDeduceStatusMsg {

    private Long orderId;
    private String status;
}
