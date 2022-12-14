package com.atguigu.gmall.constant;

/**
 * @author tkwrite
 * @create 2022-09-14-20:44
 */
public class MqConst {
    //订单交换机名
    public static final String EXCHANGE_ORDER_EVNT = "order-event-exchange";

    //订单延迟队列
    public static final String QUEUE_ORDER_DELAY = "order-delay-queue";

    //订单死信路由键
    public static final String RK_ORDER_DEAD = "order.dead";

    //订单新建路由键
    public static final String RK_ORDER_CREATED = "order.created";

    //死单队列
    public static final String QUEUE_ORDER_DEAD = "order-dead-queue";

    public static final String RK_ORDER_PAYED = "order.payed";

    //支付成功单队列
    public static final String QUEUE_ORDER_PAYED = "order-payed-queue";


    //库存交换机
    public static final String EXCHANGE_WARE_EVENT = "exchange.direct.ware.stock";


    //减库存路由键
    public static final String RK_WARE_DEDUCE = "ware.stock";

    //库存扣减结果队列
    public static final String QUEUE_WARE_ORDER = "queue.ware.order";

    //库存扣减交换机
    public static final String EXCHANGE_WARE_ORDER = "exchange.direct.ware.order";

    //库存扣减路由键
    public static final String RK_WARE_ORDER = "ware.order";

    /**
     * 减库存
     */
    public static final String EXCHANGE_DIRECT_WARE_STOCK = "exchange.direct.ware.stock";
    public static final String ROUTING_WARE_STOCK = "ware.stock";
    //队列
    public static final String QUEUE_WARE_STOCK  = "queue.ware.stock";
}
