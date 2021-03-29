package com.changgou.search.listener;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsUpListener {

    @RabbitListener(queues = "search_add_queue")
    public void searchAdd(String spuId){
        System.out.println("接收到消息："+ spuId);
    }
}
