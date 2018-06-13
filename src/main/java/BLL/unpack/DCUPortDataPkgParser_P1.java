package BLL.unpack;

import BLL.constant.dcu.CommonConvention;
import BLL.constant.dcu.Convention;
import BLL.constant.common.SizeOf;
import BLL.model.DCUCollectData;
import BLL.model.DCUPortData;
import BLL.model.DcuDataPkgInfo;
import BLL.model.DigitSignalData;
import BLL.util.BitCoverter;
import BLL.constant.dcu.AcqPotyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  冰魔方端口数据包解析类
 *修改日期：  2018-06-11 11:07.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class DCUPortDataPkgParser_P1 {
    
    private DigitSignalDataParser digitSignalDataParser = new DigitSignalDataParser();
    
    public  boolean Unpack(DCUCollectData collectData, String dtuID, DcuDataPkgInfo dcuDataPkgInfo, byte[] dtuData, int offset) {
        //端口采集数据包解包
        donUnpack(collectData, dtuData, offset);
        
        return true;
    }
    
    /**
     * 冰魔方端口采集数据解包
     * @param collectData 采集数据实体类
     * @param dtuData dtu上行数据包
     * @param offset 偏移量
     */
    private  void donUnpack(DCUCollectData collectData, byte[] dtuData, int offset) {
        int unpackedBytes = 0;
        //获取数据包ID
        collectData.setPkgID(BitCoverter.toUint16(dtuData, offset + unpackedBytes));
        unpackedBytes += SizeOf.INT_16;
        //获取采集时间
        collectData.setCollectTimestamp(BitCoverter.toUint64(dtuData, offset + unpackedBytes));
        unpackedBytes += SizeOf.INT_64;
        
        //首先打开端口部分，以便获得端口号范围。
        int first_port = dtuData[offset + unpackedBytes] & 0xFF;
        unpackedBytes += SizeOf.INT_8;
        int last_prot = dtuData[offset + unpackedBytes] & 0xFF;
        unpackedBytes += SizeOf.INT_8;
        
        unpackPortData(collectData, first_port, last_prot, dtuData, offset + unpackedBytes);
        
    }
    
    /**
     * 冰魔方端口采集数据,冰魔方的打包是按照顺序打包,并且全部发送的.
     * @param collectData 解包数据实体类
     * @param first_port 开始端口号
     * @param last_prot 最终端口号
     * @param dtuData dtu数据包
     * @param offset 偏移量
     */
    private  void unpackPortData(DCUCollectData collectData, int first_port, int last_prot, byte[] dtuData, int offset) {
        int unpackedBytes = 0;
        long collectTIME = collectData.getCollectTimestamp();
        HashMap<Byte/*端口类型*/, List<DCUPortData>> portData = collectData.getData();
        byte portTypeA = AcqPotyType.ACQ_PORT_TYPE_A;
        byte portTypeR = AcqPotyType.ACQ_PORT_TYPE_R;
        byte portTypeKWH = AcqPotyType.ACQ_PORT_TYPE_KWH;
        byte portTypeDV = AcqPotyType.ACQ_PORT_TYPE_DV;
        byte portTypeC = AcqPotyType.ACQ_PORT_TYPE_C;
        
        portData.put(portTypeA, new ArrayList<>());
        portData.put(portTypeR, new ArrayList<>());
        portData.put(portTypeKWH, new ArrayList<>());
        portData.put(portTypeDV, new ArrayList<>());
        portData.put(portTypeC, new ArrayList<>());
        
        for (int portNum = first_port; portNum <= last_prot; portNum++) {
            if (portNum >= 1 && portNum <= Convention.ACC_NUM_PORT_A) { //1-10
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                portData.get(portTypeA).add(new DCUPortData(portNum, (float) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            } else if (portNum > Convention.ACC_NUM_PORT_A && portNum <= Convention.ACC_NUM_PORT_R) {//11-20
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                portData.get(portTypeR).add(new DCUPortData(portNum - Convention.ACC_NUM_PORT_A, (float) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            } else if (portNum > Convention.ACC_NUM_PORT_R && portNum <= Convention.ACC_NUM_PORT_KWH) {//21-22
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                portData.get(portTypeKWH).add(new DCUPortData(portNum - Convention.ACC_NUM_PORT_R, data));
            } else if (portNum > Convention.ACC_NUM_PORT_KWH && portNum <= Convention.ACC_NUM_PORT_DV) {//23-32
                List<DigitSignalData> digitSignalDataList = new ArrayList<>();
                unpackedBytes += digitSignalDataParser.UnpackDigitSignalData(digitSignalDataList, collectTIME, dtuData, offset + unpackedBytes);
                portData.get(portTypeDV).add(new DCUPortData(portNum - Convention.ACC_NUM_PORT_KWH, digitSignalDataList));
            } else if (portNum > Convention.ACC_NUM_PORT_DV && portNum <= Convention.ACC_NUM_PORT_C) {//33-52
                List<DigitSignalData> digitSignalDataList = new ArrayList<>();
                unpackedBytes += digitSignalDataParser.UnpackDigitSignalData(digitSignalDataList, collectTIME, dtuData, offset + unpackedBytes);
                portData.get(portTypeC).add(new DCUPortData(portNum - Convention.ACC_NUM_PORT_DV, digitSignalDataList));
            }
        }
    }
    
    
}
