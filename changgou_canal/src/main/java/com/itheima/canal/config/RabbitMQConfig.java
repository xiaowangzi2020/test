package com.itheima.canal.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //交换机名称
    private static final String GOODS_UP_EXCHANGE="goods_up_exchange";

    //定义队列名称
    private static final String SEARCH_ADD_QUEUE="search_add_queue";

    //定义队列名称
    public static final String AD_UPDATE_QUEUE="ad_update_queue";

    //声明队列
    @Bean
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE);
    }

    //声明队列
    @Bean(AD_UPDATE_QUEUE)
    public Queue AD_UPDATE_QUEUE(){
        return new Queue(AD_UPDATE_QUEUE);
    }

    //声明交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    //队列绑定交换机
    @Bean
    public Binding AD_UPDATE_QUEUE_BINDING(@Qualifier(AD_UPDATE_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();

    }
}