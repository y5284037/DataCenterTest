package BLL.MQClient;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  生产类
 *修改日期：  2018-06-14 10:24.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class Produce {
    
    private Channel channel;
    private static BasicProperties properties;
    
//    private static
    /**
     * queue和exchange只需要声明一次即可,所以尽量避免多次声明,只有在第一次获取Produce的时候去声明.
     */
    static {
        Connection connection = RabbitFactory.getConnection();
        try {
            //为持久化属性赋值
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.deliveryMode(2);//2代表持久化,1代表不持久化
            properties = builder.build();
            
            //声明exchange和queue
            Channel channel = connection.createChannel();
            String exchange = MQTaskConfig.mqToDTUTask.getString("exchange");
            String exchangeType = MQTaskConfig.mqToDTUTask.getString("exchangeType");
            channel.exchangeDeclare(exchange, exchangeType, true, false, null);
            //将配置文件中的所有queue声明.
            for (Object queue : MQTaskConfig.dataOutQueue) {
                channel.queueDeclare((String) queue, true, false, false, null);
            }
//            channel.queueBind((String)MQTaskConfig.dataOutQueue.,)
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            System.out.println("声明exchange和queue时超时 or 连接错误,请及时处理.");
            e.printStackTrace();
            System.exit(-102);
        }
    }
    
    public Produce(String queue, String exchange, String bingingKey) {
        Connection connection = RabbitFactory.getConnection();
        try {
            channel = connection.createChannel();
            channel.queueBind(queue, exchange, bingingKey);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
