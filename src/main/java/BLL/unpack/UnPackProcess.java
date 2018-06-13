package BLL.unpack;

import BLL.constant.dcu.CommunicationProtocolVersion;
import BLL.constant.dcu.DCUDataPkgType;
import BLL.model.DCUCollectData;
import BLL.model.DCUInfo;
import BLL.model.DCUPortData;
import BLL.model.DcuDataPkgInfo;

import java.util.HashMap;
import java.util.List;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  解包类.
 *修改日期：  2018-06-06 15:43.
 *文件作者：  Arike.Y
 *
 **********************************************/

public class UnPackProcess {
    private DCUPortDataPkgParser_P1 dcuPortDataPkgParser_p1 = new DCUPortDataPkgParser_P1();
    private DCUPortDataPkgParser_P2 dcuPortDataPkgParser_p2 = new DCUPortDataPkgParser_P2();
    private DCUDataPkgParser dcuDataPkgParser = new DCUDataPkgParser();
    
    /**
     * @param dtuID
     * @param dtuData
     */
    public void unpackData(String dtuID, byte[] dtuData) {
        DcuDataPkgInfo dcuDataPkgInfo = new DcuDataPkgInfo();
        dcuDataPkgInfo.setRecvTime(System.currentTimeMillis());
        dcuDataPkgInfo.setDcuInfo(new DCUInfo());
        int unpackedBytes = dcuDataPkgParser.unpackDCUDataPkgInfo(dcuDataPkgInfo, dtuData);
        unpackedBytes = dcuDataPkgParser.unpackDCUInfo(dcuDataPkgInfo.getDcuInfo(), dtuData, unpackedBytes);
        
        DCUCollectData collectData = new DCUCollectData();
        switch (dcuDataPkgInfo.getProtocolVerNum()) {
            case CommunicationProtocolVersion.
                    PROTOCOL_1:
                HandleDCUDataPkg_P1(dtuID, dcuDataPkgInfo, collectData, dtuData, unpackedBytes);
                break;
            case CommunicationProtocolVersion.
                    PROTOCOL_2:
                HandleDCUDataPkg_P2(dtuID, dcuDataPkgInfo, collectData, dtuData, unpackedBytes);
                break;
        }
        System.out.println(dcuDataPkgInfo);
        System.out.println(collectData);
    }
    
    
    private void HandleDCUDataPkg_P1(String dtuID, DcuDataPkgInfo dcuDataPkgInfo, DCUCollectData collectData, byte[] dtuData, int offset) {
        
        collectData.setData(new HashMap<Byte, List<DCUPortData>>());
        switch (dcuDataPkgInfo.getDataPkgType()) {
            //端口数据包
            case DCUDataPkgType.
                    DCU_PORT_ACQ_DATA:
                if (dcuPortDataPkgParser_p1.Unpack(collectData, dtuID, dcuDataPkgInfo, dtuData, offset)) {
                    
                }
                
                break;
            //时间同步请求包
            case DCUDataPkgType
                    .TIME_SYNC_REQUEST:
                
                ;
                break;
            
        }
        
    }
    
    private void HandleDCUDataPkg_P2(String dtuID, DcuDataPkgInfo dcuDataPkgInfo, DCUCollectData collectData, byte[] dtuData, int offset) {
        
        collectData.setData(new HashMap<Byte, List<DCUPortData>>());
        switch (dcuDataPkgInfo.getDataPkgType()) {
            case DCUDataPkgType.
                    DCU_PORT_ACQ_DATA:
                if (dcuPortDataPkgParser_p2.Unpack(collectData, dtuID, dcuDataPkgInfo, dtuData, offset)) {
                
                }
                break;
        }
    }
    
}
