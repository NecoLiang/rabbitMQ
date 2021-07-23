package com.atyiche.rabbitmq.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author liangyt
 * @create 2021-07-19 15:55
 * ttl队列优化
 */
@Configuration
public class MsgTtlQueueConfig {
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String QUEUE_C = "QC";
    //声明队列C 绑定交换机
    @Bean("queueC")
    public Queue queuec(){
        HashMap<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(args).build();
    }
    @Bean
    public Binding queuecBindngX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange")DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }
}
