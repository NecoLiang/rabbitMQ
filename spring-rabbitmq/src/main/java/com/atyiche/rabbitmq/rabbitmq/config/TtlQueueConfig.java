package com.atyiche.rabbitmq.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangyt
 * @create 2021-07-15 16:22
 * 延迟队列相关配置
 */
@Configuration
public class TtlQueueConfig {
    //普通交换机
    private static final String X_CHANGE="x";
    //普通队列
    private static final String QUEUE_A="QA";
    private static final String QUEUE_B="QB";

    //死信交换机
    private static final String Y_DEAD_LETTER_EXCHANGE="Y";
    //死信队列
    private static final String DEAD_LETTER_QUEUE="QD";

    //声明普通交换机X
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_CHANGE);
    }

    //声明死信交换机Y
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明队列A ttl10秒，绑定对应的死信交换机
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }
    //声明队列A 绑定X交换机
    @Bean
    public Binding queueABindingX(@Qualifier("xExchange")DirectExchange xExchange, @Qualifier("queueA")Queue queueA){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //声明队列B ttl10秒，绑定对应的死信交换机
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(args).build();
    }
    //声明队列B 绑定X交换机
    @Bean
    public Binding queueBBindingX(@Qualifier("xExchange")DirectExchange xExchange, @Qualifier("queueB")Queue queueB){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    //声明死信队列QD
    @Bean("queueD")
    public Queue queueD(){
        return new Queue(DEAD_LETTER_QUEUE);
    }

    //声明QD和死信交换机的绑定关系
    @Bean
    public  Binding queueDBindingy(@Qualifier("yExchange")DirectExchange yExchange, @Qualifier("queueD")Queue queueD){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
