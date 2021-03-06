package BLL.unpack;

import BLL.MQClient.Produce;
import BLL.constant.dcu.CommunicationProtocolVersion;
import BLL.constant.dcu.DCUDataPkgType;
import BLL.model.*;
import BLL.util.ServerRecvDCUPortDataAck;
import com.alibaba.fastjson.JSONObject;

import java.util.Base64;
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
    private TimeSyncHandler timeSyncHandler = new TimeSyncHandler();
    private HashMap<Long, ServerRecvDCUPortDataAck> dataPkgAcks = new HashMap<>();
    private Base64.Encoder encoder = Base64.getEncoder();
    private Produce produce = new Produce();
    
    /**
     * 解包数据函数
     *
     * @param routingKey 路由键
     * @param dtuID      数据传输单元ID
     * @param dtuData    DCU采集数据包
     */
    public void unpackData(String routingKey, String dtuID, byte[] dtuData) {
        DcuDataPkgInfo dcuDataPkgInfo = new DcuDataPkgInfo();
        dcuDataPkgInfo.setRecvTime(System.currentTimeMillis());
        dcuDataPkgInfo.setDcuInfo(new DCUInfo());
        int unpackedBytes = dcuDataPkgParser.unpackDCUDataPkgInfo(dcuDataPkgInfo, dtuData);
        unpackedBytes = dcuDataPkgParser.unpackDCUInfo(dcuDataPkgInfo.getDcuInfo(), dtuData, unpackedBytes);
        
        DCUCollectData collectData = new DCUCollectData();
        switch (dcuDataPkgInfo.getProtocolVerNum()) {
            case CommunicationProtocolVersion.PROTOCOL_1:
                HandleDCUDataPkg_P1(routingKey, dtuID, dcuDataPkgInfo, collectData, dtuData, unpackedBytes);
                break;
            case CommunicationProtocolVersion.PROTOCOL_2:
                HandleDCUDataPkg_P2(routingKey, dtuID, dcuDataPkgInfo, collectData, dtuData, unpackedBytes);
                break;
            default:
        }
        System.out.println(dcuDataPkgInfo);
        System.out.println(collectData);
    }
    
    /**
     * 冰魔方数据处理函数
     *
     * @param routingKey     路由键
     * @param dtuID          数据传输单元ID
     * @param dcuDataPkgInfo DCU数据包信息
     * @param collectData    DCU端口采集数据
     * @param dtuData        DCU采集数据包
     * @param offset         偏移量
     */
    private void HandleDCUDataPkg_P1(String routingKey, String dtuID, DcuDataPkgInfo dcuDataPkgInfo, DCUCollectData collectData, byte[] dtuData, int offset) {
        
        collectData.setData(new HashMap<Byte, List<DCUPortData>>());
        switch (dcuDataPkgInfo.getDataPkgType()) {
            //端口数据包
            case DCUDataPkgType.DCU_PORT_ACQ_DATA:
                if (dcuPortDataPkgParser_p1.Unpack(collectData, dtuID, dcuDataPkgInfo, dtuData, offset)) {
                    HandleDCUPortData(routingKey, dtuID, collectData, dcuDataPkgInfo);
                }
                break;
            //时间同步请求包
            case DCUDataPkgType.TIME_SYNC_REQUEST:
                timeSyncHandler.handleTimeSyncRequest(routingKey, dtuID, dcuDataPkgInfo.getDcuInfo(), dtuData, offset);
                break;
            default:
        }
    }
    
    /**
     * 冰心数据处理函数
     *
     * @param routingKey     路由键
     * @param dtuID          数据传输单元ID
     * @param dcuDataPkgInfo DCU数据包信息
     * @param collectData    DCU端口采集数据
     * @param dtuData        DCU采集数据包
     * @param offset         偏移量
     */
    private void HandleDCUDataPkg_P2(String routingKey, String dtuID, DcuDataPkgInfo dcuDataPkgInfo, DCUCollectData collectData, byte[] dtuData, int offset) {
        collectData.setData(new HashMap<Byte, List<DCUPortData>>());
        switch (dcuDataPkgInfo.getDataPkgType()) {
            case DCUDataPkgType.DCU_PORT_ACQ_DATA:
                if (dcuPortDataPkgParser_p2.Unpack(collectData, dtuID, dcuDataPkgInfo, dtuData, offset)) {
                    HandleDCUPortData(routingKey, dtuID, collectData, dcuDataPkgInfo);
                }
                break;
            //时间同步请求包
            case DCUDataPkgType.TIME_SYNC_REQUEST:
                timeSyncHandler.handleTimeSyncRequest(routingKey, dtuID, dcuDataPkgInfo.getDcuInfo(), dtuData, offset);
                break;
        }
    }
    
    /**
     * 数据解析后处理函数
     *
     * @param routingKey     路由键
     * @param dtuID          数据传输单元ID
     * @param collectData    DCU端口采集数据
     * @param dcuDataPkgInfo DCU数据包信息
     */
    private void HandleDCUPortData(String routingKey, String dtuID, DCUCollectData collectData, DcuDataPkgInfo dcuDataPkgInfo) {
        // 处理一个收到的合法的、非重复的数据包
        HandleNewPortDataPkg(routingKey, dtuID, collectData, dcuDataPkgInfo);
    }
    
    /**
     * 处理一个新的合法的数据包((进行回执调用以及持久化))
     *
     * @param routingKey     路由键
     * @param dtuID          数据传输单元ID
     * @param collectData    DCU端口采集数据
     * @param dcuDataPkgInfo DCU数据包信息
     */
    private void HandleNewPortDataPkg(String routingKey, String dtuID, DCUCollectData collectData, DcuDataPkgInfo dcuDataPkgInfo) {
        long dcuID = dcuDataPkgInfo.getDcuInfo().getDcuID();
        int pkgID = collectData.getPkgID();
        long collectTimestamp = collectData.getCollectTimestamp();
        
        
        // 5.向监测器返回数据包接收回执
        AckRecvDCUPortDataPkg(routingKey, dtuID, dcuID, pkgID, collectTimestamp);
    }
    
    /**
     * 发送数据包回执
     *
     * @param dtuID            数据传输单元ID
     * @param dcuID            数据采集单元ID
     * @param pkgID            DCU数据包ID
     * @param collectTimestamp DCU数据采集时间
     */
    private void AckRecvDCUPortDataPkg(String rouyingKey, String dtuID, long dcuID, int pkgID, long collectTimestamp) {
        ServerRecvDCUPortDataAck serverRecvDCUPortDataAck;
        if (dataPkgAcks.containsKey(dcuID)) {
            serverRecvDCUPortDataAck = dataPkgAcks.get(dcuID);
        } else {
            serverRecvDCUPortDataAck = new ServerRecvDCUPortDataAck();
            dataPkgAcks.put(dcuID, serverRecvDCUPortDataAck);
        }
        serverRecvDCUPortDataAck.ackQueue.offer(serverRecvDCUPortDataAck.new RecvDCUPortDataAck((int) dcuID, (short) pkgID));
        JSONObject dtuOutData = new JSONObject();
        dtuOutData.put("DTUID", dtuID);
        dtuOutData.put("Data", encoder.encodeToString(serverRecvDCUPortDataAck.Serialize()));
        produce.publish(rouyingKey, dtuOutData.toString().getBytes());
    }
}
