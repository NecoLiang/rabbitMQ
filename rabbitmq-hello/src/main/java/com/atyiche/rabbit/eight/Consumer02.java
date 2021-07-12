package com.atyiche.rabbit.eight;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangyt
 * @create 2021-07-12 14:18
 *
 * 死信队列消费者01
 */
public class Consumer02 {
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitUtils.getChannel();
        channel.exchangeDeclare(DEAD_EXCHANGE,BuiltinExchangeType.DIRECT);
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接收死信队列的消息");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println("Consumer02接收到死信队列消息"+message);
        };
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag->{});
    }
}
