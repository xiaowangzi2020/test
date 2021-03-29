package com.itheima.canal.listener;


import com.alibaba.otter.canal.protocol.CanalEntry;
import com.itheima.canal.utils.CanalUtil;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@CanalEventListener
public class SpuListener01 {


    @Autowired
    private RabbitTemplate rabbitTemplate;




    @ListenPoint(schema = "changgou_goods", table = {"tb_spu"})
    public void spu(CanalEntry.EventType eventType, CanalEntry.RowData rowData){



        String before = CanalUtil.convertToMap(rowData.getBeforeColumnsList()).get("is_marketable");
        String after = CanalUtil.convertToMap(rowData.getAfterColumnsList()).get("is_marketable");

        System.out.println(before);
        System.out.println(after);
        System.out.println(CanalUtil.convertToMap(rowData.getAfterColumnsList()).get("id"));


        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {

                if(ack){
                    System.out.println("商品上架消息接收成功");
                }else{
                    System.out.println("商品上架消息接收失败" + cause);
                }

            }
        });

        if("0".equals(before) && "1".equals(after)){
            System.out.println("执行了");
            rabbitTemplate.convertAndSend("test_exchange", "", CanalUtil.convertToMap(rowData.getAfterColumnsList()).get("id"));
        }
    }
}
