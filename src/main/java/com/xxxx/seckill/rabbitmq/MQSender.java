package com.xxxx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version v1.0
 * @ClassName: MQSender
 * @Description: 消息发送者
 * @Author: ChenQ
 * @Date: 2022/9/8 on 20:42
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void send(Object msg){
        log.info("发送消息："+msg);
        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
    }
}
