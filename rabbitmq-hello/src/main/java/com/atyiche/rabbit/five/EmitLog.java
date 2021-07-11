package com.atyiche.rabbit.five;

import com.atyiche.rabbit.utils.RabbitUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * @author liangyt
 * @create 2021-07-10 10:52
 */
public class EmitLog {
    private static final String EXCHANGE_NAME = "logs";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitUtils.getChannel()) {
            /**
             * 声明一个 exchange
             * 1.exchange 的名称
             * 2.exchange 的类型
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入信息");
            while (sc.hasNext()) {
                String message = sc.nextLine();
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println("生产者发出消息" + message);
            }
        }
    }
}
