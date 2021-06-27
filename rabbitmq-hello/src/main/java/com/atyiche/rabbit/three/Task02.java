package com.atyiche.rabbit.three;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * @author liangyt
 * @create 2021-06-22 16:50
 */
public class Task02 {
    private static final String TASK_QUEUE_NAME = "ack_queue";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitUtils.getChannel()) {
            //参数2创建队列的时候持久化队列
            boolean durable = true;//声明队列持久化
            channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入信息");
            while (sc.hasNext()) {
                String message = sc.nextLine();
                //参数是为了消息持久化（MessageProperties.PERSISTENT_TEXT_PLAIN）
                channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                System.out.println("生产者发出消息" + message);
            }
        }
    }

}
