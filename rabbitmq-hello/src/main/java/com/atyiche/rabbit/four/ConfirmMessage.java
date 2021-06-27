package com.atyiche.rabbit.four;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.Channel;

import java.util.UUID;

/**
 * @author liangyt
 * @create 2021-06-27 10:28
 * MQ的发布确认模式
 * 使用时选择哪个是最优的
 * 1.单个确认
 * 2.批量确认
 * 3.异步批量确认
 */
public class ConfirmMessage {
    public static final int msg_count = 1000;
    public static void main(String[] args) throws Exception {
        ConfirmMessage.publishMsgIndividually();
    }
    public static void publishMsgIndividually() throws Exception {
        Channel channel = RabbitUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i <msg_count; i++) {
            String msg = i+"";
            channel.basicPublish("",queueName,null,msg.getBytes());
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发布成功");

            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("发送"+msg_count+"条消息耗时"+(endTime-startTime)+"ms");
    }
}
