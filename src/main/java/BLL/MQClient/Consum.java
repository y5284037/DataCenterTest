package BLL.MQClient;

import BLL.constant.common.MqConfig;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  Rabbit消费者(接收端).
 *修改日期：  2018-06-06 15:43.
 *文件作者：  Arike.Y
 *
 **********************************************/

public class Consum {
    
    
    public  void startConsum() {
        startDataConsum();
        startEventConsum();
    }
    
    private  void startEventConsum() {
        try {
            Channel eventChannel = RabbitFactory.getConnection().createChannel(1001);
            eventChannel.queueBind(MqConfig.EVENT_QUEUE, MqConfig.EVENT_EXCHANGE, MqConfig.EVENT_ROUTINGKEY);
            EventConsumer eventConsumer = new EventConsumer(eventChannel);
            eventChannel.basicConsume(MqConfig.EVENT_QUEUE, false, eventConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private  void startDataConsum() {
        try {
            Channel dataChannel = RabbitFactory.getConnection().createChannel(1000);
            dataChannel.queueBind(MqConfig.DATA_QUEUE, MqConfig.DATA_EXCHANGE, MqConfig.DATA_ROUTINGKEY);
            DataConsumer dataConsumer = new DataConsumer(dataChannel);
            dataChannel.basicConsume(MqConfig.DATA_QUEUE, false, dataConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
