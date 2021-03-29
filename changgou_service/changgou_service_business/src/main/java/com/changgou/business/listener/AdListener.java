package com.changgou.business.listener;


import com.changgou.business.service.AdService;
import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "ad_update_queue")
public class AdListener {

    @Autowired
    private AdService adService;

    @RabbitHandler
    public void adUpdate(String message) {
        System.out.println("接收到消息" + message);


        //发起远程调用
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://192.168.200.128/ad_update?position=" + message;
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功
                System.out.println("请求成功:" + response.message());
            }
        });
    }
}
