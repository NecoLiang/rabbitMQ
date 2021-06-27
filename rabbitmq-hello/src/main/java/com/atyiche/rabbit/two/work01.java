package com.atyiche.rabbit.two;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author liangyt
 * @create 2021-06-22 16:18
 * 工作模式的工作线程（消费者）
 */
public class work01 {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitUtils.getChannel();
        //函数式接口的lambda表达式写法，直接传入函数式接口中方法所需参数
        //消息接收回调
        DeliverCallback deliverCallback=(consumerTag, delivery)->{
            String receivedMessage = new String(delivery.getBody());
            System.out.println("接收到消息:"+receivedMessage);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        //消息取消回调
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };
        System.out.println("C2 消费者启动等待消费......");
        channel.basicConsume(QUEUE_NAME,false,deliverCallback,cancelCallback);
    }

}

