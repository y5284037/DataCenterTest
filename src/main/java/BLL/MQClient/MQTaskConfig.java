package BLL.MQClient;

import BLL.util.JSONCoverter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.HashMap;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  MQ配置文件存放类.
 *修改日期：  2018-06-14 10:57.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class MQTaskConfig {
    
    public static JSONObject dtuDataToMQTask;
    public static JSONObject dtuEventToMQTask;
    public static JSONObject mqToDTUTask;
    public static JSONArray dataOutQueue;
    
    static {
        JSONObject MQConfig = JSONCoverter.JsonFileToMap("MQTaskConfig.json");
        dtuDataToMQTask = MQConfig.getJSONObject("dtuDataToMQTask");
        dtuEventToMQTask = MQConfig.getJSONObject("dtuEventToMQTask");
        mqToDTUTask = MQConfig.getJSONObject("mqToDTUTask");
        dataOutQueue = (JSONArray)mqToDTUTask.get("queue");
    }
    
    public static void main(String[] args) {
        System.out.println(mqToDTUTask.get(""));
    }
}
