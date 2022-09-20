package com.atguigu.gmall.model.enums;
//OrderStatus 更细粒度的状态，用于记录记录订单状态变化日志
public enum ProcessStatus {
    UNPAID("未支付", OrderStatus.UNPAID),
    PAID("已支付", OrderStatus.PAID),
    NOTIFIED_WARE("已通知仓储", OrderStatus.PAID),
    WAITING_DELEVER("待发货", OrderStatus.WAITING_DELEVER),
    STOCK_EXCEPTION("库存异常", OrderStatus.PAID),
    DELEVERED("已发货", OrderStatus.DELEVERED),
    STOCK_OVER_EXCEPTION("超出库存异常", OrderStatus.WAITING_SCHEDULE),
    CLOSED("已关闭", OrderStatus.CLOSED),
    FINISHED("已完结", OrderStatus.FINISHED) ,
    PAY_FAIL("支付失败", OrderStatus.UNPAID),
    SPLIT("订单已拆分", OrderStatus.SPLIT);


    private String comment ;
    private OrderStatus orderStatus;

    ProcessStatus(String comment, OrderStatus orderStatus){
        this.comment=comment;
        this.orderStatus=orderStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
