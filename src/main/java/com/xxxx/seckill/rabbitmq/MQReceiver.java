package com.xxxx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @version v1.0
 * @ClassName: MQReceiver
 * @Description:
 * @Author: ChenQ
 * @Date: 2022/9/8 on 20:44
 */
@Service
@Slf4j
public class MQReceiver {
    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接收消息："+msg);
    }
    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("QUEUE01接收消息："+msg);
    }
    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("QUEUE02接收消息："+msg);
    }

}
