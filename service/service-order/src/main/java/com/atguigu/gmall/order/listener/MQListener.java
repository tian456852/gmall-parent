package com.atguigu.gmall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tkwrite
 * @create 2022-09-14-18:10
 */
@Slf4j
@Component
public class MQListener {
    //Map搬到redis
    private ConcurrentHashMap<String,AtomicInteger> counter=new ConcurrentHashMap<>();

     // @RabbitListener(queues = "haha")
    public void listenerHaha(Message message, Channel channel) throws IOException {
        String content = new String(message.getBody());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
            counter.putIfAbsent(content,new AtomicInteger(0));

        try {

        System.out.println("收到消息："+content);
         int i=10/0;
        //处理业务
        channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            log.error("消息消费失败：{}",content);
            AtomicInteger integer = counter.get(content);
            System.out.println(deliveryTag+"加到"+integer);
            if (integer.incrementAndGet()<3){
            //    重新存储这个消息，待下个人继续处理
            channel.basicNack(deliveryTag,false,true);
            }else {
            //    超过了最大重试次数
            //    TODO 把超过重试次数的消息放到数据库专门一张表，重回失败消息表
            //    TODO 人工补偿（1、人工修改  2、业务BUG修改完以后重新发送这些消息让业务继续消费）
                log.error("{}消息重试10次依然失败，已经记录到数据库等待人工处理",content);
                channel.basicNack(deliveryTag,false,false);
                counter.remove(content);
            }
        }
    }

}
