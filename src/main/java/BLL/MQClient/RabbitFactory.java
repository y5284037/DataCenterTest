package BLL.MQClient;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  Rabbit工厂类,负责生成Rabbit的各种接口.
 *修改日期：  2018-06-06 15:43.
 *文件作者：  Arike.Y
 *
 **********************************************/

public class RabbitFactory {
    
    private static Logger logger = Logger.getLogger(RabbitFactory.class);
    private static String rabbitHost = "j2web.cn";
    private static String rabbitUserName = "guest";
    private static String rabbitPassword = "guest";
    private static ConnectionFactory connectionFacory = new ConnectionFactory();
    private static Connection connection;
    
    /**
     * 获取一个新的RabbitMQ Socket连接
     *
     * @return
     */
    public static Connection getConnection() {
        connectionFacory.setHost(rabbitHost);
        connectionFacory.setUsername(rabbitUserName);
        connectionFacory.setPassword(rabbitPassword);
        try {
            connection = connectionFacory.newConnection();
        } catch (IOException e) {
            logger.error("IO连接异常.获取连接失败.");
            e.printStackTrace();
        } catch (TimeoutException e) {
            logger.error("连接超时,获取连接失败.");
            e.printStackTrace();
        }
        return connection;
    }
}
