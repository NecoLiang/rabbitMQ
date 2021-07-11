package com.atyiche.rabbit.six;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.*;

/**
 * @author liangyt
 * @create 2021-07-11 15:39
 */
public class ReceiveLogsDirect01 {
    private static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws Exception {
        //创建信道
        Channel channel = RabbitUtils.getChannel();
        //信道定义直连交换机(对应不同的routingkey)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String quueName = "console";
        //信道定义队列
        channel.queueDeclare(quueName,false,false,false,null);
        //绑定队列
        channel.queueBind(quueName,EXCHANGE_NAME,"info");
        channel.queueBind(quueName,EXCHANGE_NAME,"warning");
        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            String message= new String(delivery.getBody(),"UTF-8");
            System.out.println("接收绑定键"+delivery.getEnvelope().getRoutingKey()+",消息"+message+",标识"+delivery.getEnvelope().getDeliveryTag());

        };
        //消息接收
        channel.basicConsume(quueName,false, deliverCallback, consumerTag->{});




    }
}
