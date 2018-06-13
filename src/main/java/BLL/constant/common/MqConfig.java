package BLL.constant.common;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  存放RabbitMQ需要的各种配置常量
 *修改日期：  2018-06-06 15:43.
 *文件作者：  Arike.Y
 *
 **********************************************/

public class MqConfig {
    public static final String DATA_QUEUE = "dtu.data.in";
    public static final String EVENT_QUEUE = "dtu.event.in";
    public static final String OUT_QUEUE = "dtu.data.out.cdzs";
    
    public static final String DATA_ROUTINGKEY = "dtu.cdzs.data";
    public static final String EVENT_ROUTINGKEY = "dtu.cdzs.event";
    public static final String OUT_ROUTING = "dtu.data.out.cdzs";
    
    public static final String DATA_EXCHANGE = "dtu.data.in";
    public static final String EVENT_EXCHANGE = "dtu.event.in";
    public static final String OUT_EXCHANGE = "dtu.data.out";
    
    public static final String EXCHANGE_TYPE = "direct";
    
}
