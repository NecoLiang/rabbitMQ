package com.atyiche.rabbit.four;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

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

    public static void publishMsgAsync() throws Exception {
        Channel channel = RabbitUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        //消息控制队列，支持高并发
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        long startTime = System.currentTimeMillis();

        //消息确认成功回调函数
        /**
         * 1.消息序列号
         * 2.true可以确认小于等于当前序列号信息
         * false确认当前序列号
         */
        ConfirmCallback ackConfirmBack = (deliveryTag,multiple)->{
            if (multiple){
                //返回的是小于等于当前序列号的未确认消息，是一个map
                ConcurrentNavigableMap<Long, String> confirmd = outstandingConfirms.headMap(deliveryTag, true);
                //清除确认完的消息
                confirmd.clear();
            }else {
                //清除当前序列号的消息
                outstandingConfirms.remove(deliveryTag);

            }
            System.out.println("消息已消费清除");
        };
        //消息确认失败回调函数
        ConfirmCallback nackCallBack = (deliveryTag,multiple)->{
            String s = outstandingConfirms.get(deliveryTag);
            System.out.println("未消费消息");
        };
        //准备消息监听器，监听消息成功与否（异步通知）
        channel.addConfirmListener(ackConfirmBack,nackCallBack);

        for (int i = 0; i < msg_count; i++) {
            String msg = i+"";
            channel.basicPublish("",queueName,null,msg.getBytes());

        }
        long endTime = System.currentTimeMillis();
        System.out.println("发送"+msg_count+"条消息耗时"+(endTime-startTime)+"ms");
    }


}
