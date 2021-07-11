package com.atyiche.rabbit.seven;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author liangyt
 * @create 2021-07-11 16:41
 */
public class ReceiveLogsTopic02 {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明Q1队列与绑定关系
        String queueName = "Q2";
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName, EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(queueName, EXCHANGE_NAME, "lazy.#");
        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println("接收队列"+queueName+"绑定键"+delivery.getEnvelope().getRoutingKey()+",消息"+message);

        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});

    }
}
