package BLL.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  DCU硬件型号编号类
 *修改日期：  2018-06-07 14:41.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class DCUHwModel {
    
    private static Logger logger = Logger.getLogger(DCUHwModel.class);
    private static JSONObject hwModel;
    
    static {
        hwModel = new JSONObject();
        LoadHardwareModelConfig();//在静态代码块中执行是为了作为驱动加载一次
    }
    
    @SuppressWarnings("unchecked")
    private static void LoadHardwareModelConfig() {
        hwModel = JSONCoverter.JsonFileToMap("HardwareModelConfig.json");
    }
    
    public static String getName(int modelNum){
        
        return hwModel.getString(Integer.toString(modelNum));
    }
    
}
