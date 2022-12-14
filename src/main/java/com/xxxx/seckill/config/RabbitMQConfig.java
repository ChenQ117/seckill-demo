package com.xxxx.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @version v1.0
 * @ClassName: RabbitMQConfig
 * @Description: RabbitMQ配置
 * @Author: ChenQ
 * @Date: 2022/9/8 on 20:39
 */
@Configuration
public class RabbitMQConfig {
//    private static final String QUEUE01 = "queue_fanout01";
//    private static final String QUEUE02 = "queue_fanout02";
//    private static final String EXCHANGE = "fanoutExchange";
//    @Bean
//    public Queue queue(){
//        return new Queue("queue",true);
//    }
//    @Bean
//    public Queue queue01(){
//        return new Queue(QUEUE01);
//    }
//    @Bean
//    public Queue queue02(){
//        return new Queue(QUEUE02);
//    }
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return new FanoutExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01(){
//        return BindingBuilder.bind(queue01()).to(fanoutExchange());
//    }
//    @Bean
//    public Binding binding02(){
//        return BindingBuilder.bind(queue02()).to(fanoutExchange());
//    }
}
