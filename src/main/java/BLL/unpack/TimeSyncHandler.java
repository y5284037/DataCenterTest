package BLL.unpack;

import BLL.constant.common.SizeOf;
import BLL.model.DCUInfo;
import BLL.util.BitCoverter;
import BLL.util.ServerTimeSyncReply;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  时间同步处理类
 *修改日期：  2018-06-13 10:24.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class TimeSyncHandler {
    
    public  void handleTimeSyncRequest(String dtuID, DCUInfo dcuInfo, byte[] dtuData, int offset) {
        ServerTimeSyncReply reply = new ServerTimeSyncReply();
//        reply.setDcuTimeSyncReqID();
        long dcuTimeSyncReqID = 0;
        
        unpackTimeSyncReqPkg(dcuTimeSyncReqID, dtuData, offset);
    
        long currentTime = System.currentTimeMillis();
        
    }
    
    private  int unpackTimeSyncReqPkg(long dcuTimeSyncReqID, byte[] dtuData, int offset) {
    
        dcuTimeSyncReqID = BitCoverter.toUint64(dtuData, offset);
        return SizeOf.INT_64;
    }
}
