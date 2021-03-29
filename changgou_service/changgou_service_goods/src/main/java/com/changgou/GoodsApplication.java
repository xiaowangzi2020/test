package com.changgou;

import com.changgou.utils.IdWorker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication {

    @Value("${workerId}")
    private long workerId;

    @Value("${datacenterId}")
    private long datacenterId;


    public static void main(String[] args) {
        SpringApplication.run( GoodsApplication.class);
    }


    /**
     * 分布式Id生成Bean
     * @return
     */
    @Bean
    public IdWorker getIdWork(){
        return new IdWorker(workerId, datacenterId);
    }

}
