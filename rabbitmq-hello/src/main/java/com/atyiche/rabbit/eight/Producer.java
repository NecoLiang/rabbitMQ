package com.atyiche.rabbit.eight;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * @author liangyt
 * @create 2021-07-12 14:40
 *
 * 死信队列消息生产者
 */
public class Producer {
    //发送的交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitUtils.getChannel();
       channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
       //设置消息的TTL时间
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        //队列个数限制
        for (int i = 1; i <11; i++) {
            String message = "info"+i;
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",null,message.getBytes());
            System.out.println("生产者发送消息:"+message);
        }
    }
}
