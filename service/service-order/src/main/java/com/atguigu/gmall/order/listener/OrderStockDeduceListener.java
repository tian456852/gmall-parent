package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceStatusMsg;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.serviec.RabbitService;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author tkwrite
 * @create 2022-09-19-20:54
 */


@Slf4j
@Service
public class OrderStockDeduceListener {

    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    RabbitService rabbitService;
    /**
     * 支付成功扣减库存结果
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_WARE_ORDER,
                            durable = "true",exclusive = "false",autoDelete = "false"),
                    exchange = @Exchange(name = MqConst.EXCHANGE_WARE_ORDER,
                            durable = "true",autoDelete = "false",type = "direct"),
                    key = MqConst.RK_WARE_ORDER
            )
    })
    public void stockDeduceListener(Message message,Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        //    TODO 按照库存扣减结果
        WareDeduceStatusMsg msg = Jsons.toObj(message, WareDeduceStatusMsg.class);
        Long orderId = msg.getOrderId();
     try {
        log.info("订单服务，监听到库存扣减结果：{}",msg);
    // 查询用户id
    //    方式1.改造业务：每次发消息的时候都带上
    //    方式2.Sharding：不带分片键（查所有库所有表）、增删改不行

        //查询订单
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        ProcessStatus status = null;
        switch (msg.getStatus()){
            case "DEDUCTED": status = ProcessStatus.WAITING_DELEVER;break;
            case "OUT_OF_STOCK": status = ProcessStatus.STOCK_OVER_EXCEPTION;break;
            default: status = ProcessStatus.PAID;
        }

        orderInfoService.changeOrderStatus(orderId,
                                           orderInfo.getUserId(),
                                           status,
                                           Arrays.asList(ProcessStatus.PAID)
        );
        channel.basicAck(tag,false);
    }catch (Exception e){
        String uk = SysRedisConst.MQ_RETRY + "stock:order:deduce:" +orderId ;
        rabbitService.retryConsumMsg(10L,uk,tag,channel);
    }
  }

}
