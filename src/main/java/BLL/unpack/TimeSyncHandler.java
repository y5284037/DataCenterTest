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
        long dcuTimeSyncReqID = BitCoverter.toUint64(dtuData, offset);
//        unpackTimeSyncReqPkg(dcuTimeSyncReqID, dtuData, offset);
        long currentTime = System.currentTimeMillis();
        doTimeSyncRequest(dtuID, dcuInfo, dcuTimeSyncReqID, currentTime);
        
        
    }
    
    private boolean doTimeSyncRequest(String dtuID, DCUInfo dcuInfo, long dcuTimeSyncReqID, long currentTime) {
        ServerTimeSyncReply reply = new ServerTimeSyncReply();
        reply.setDcuTimeSyncReqID(dcuTimeSyncReqID);
        reply.setServerTime(currentTime);
        return true;
    }
    
    
    /**
     * java不支持指针传递,所以只有把这个函数写到执行函数内,否则就需要使用对象对基本数据进行包装,在对象内操作.无意义
     * @param dcuTimeSyncReqID
     * @param dtuData
     * @param offset
     * @return
     */
    private  int unpackTimeSyncReqPkg(long dcuTimeSyncReqID, byte[] dtuData, int offset) {
        dcuTimeSyncReqID=BitCoverter.toUint64(dtuData, offset);
        return SizeOf.INT_64;
    }
}
