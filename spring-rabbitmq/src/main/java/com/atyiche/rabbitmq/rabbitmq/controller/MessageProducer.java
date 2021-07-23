package com.atyiche.rabbitmq.rabbitmq.controller;

import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

/**
 * @author liangyt
 * @create 2021-07-22 11:50
 */
@Slf4j
@Component
public class MessageProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    //注入rabbitTemplate后，设置该值
    @PostConstruct
    private void init(){
        rabbitTemplate.setConfirmCallback(this);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnsCallback(this);
    }

    @GetMapping("sendMessage")
    public void sendMessage(String message){
        //让消息绑定一个 id 值
        CorrelationData correlationData1 = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("confirm.exchange","key1",message+"key1",correlationData1)
        ;
        log.info("发送消息 id 为:{}内容为{}",correlationData1.getId(),message+"key1");
        CorrelationData correlationData2 = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("confirm.exchange","key2",message+"key2",correlationData2)
        ;
        log.info("发送消息 id 为:{}内容为{}",correlationData2.getId(),message+"key2");
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机收到消息确认成功, id:{}", id);
        } else {
            log.error("消息 id:{}未成功投递到交换机,原因是:{}", id, cause);
        }

    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("消息:{}被服务器退回，退回原因:{}, 交换机是:{}, 路由 key:{}",
                new String(returned.getMessage().getBody()),returned.getReplyText(),returned.getExchange(), returned.getRoutingKey());
    }
}
