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
public class Consumer01 {
    //普通交换机名称
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //普通队列
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitUtils.getChannel();
        //声明普通交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE,BuiltinExchangeType.DIRECT);
        //声明普通队列（配置发送死信队列参数）
        Map<String, Object> params = new HashMap<>();
        //普通队列配置死信交换机
        params.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //普通队列配置死信routing-key，为固定值
        params.put("x-dead-letter-routing-key","lisi");
        //普通队列配置长度限制
//        params.put("x-max-length",6);
        channel.queueDeclare(NORMAL_QUEUE,false,false,false, params);

        //普通队列绑定交换机和routing-key
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");

        //声明死信接收队列（普通队列）
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);
        //死信队列绑定
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接收消息");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");
            if (message.equals("info5")){
                System.out.println("Consumer01拒绝接收消息" + message);
                //拒绝且不放回队列
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
            }else {
                System.out.println("Consumer01接收到消息" + message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        };
        //关闭自动ack
        boolean autoAck = false;
        channel.basicConsume(NORMAL_QUEUE,autoAck,deliverCallback,consumerTag->{});
    }
}
