package BLL.unpack;

import BLL.constant.common.SizeOf;
import BLL.constant.dcu.AcqPotyType;
import BLL.constant.dcu.CommonConvention;
import BLL.model.DCUCollectData;
import BLL.model.DCUPortData;
import BLL.model.DcuDataPkgInfo;
import BLL.model.DigitSignalData;
import BLL.util.BitCoverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  冰心端口数据包解析类
 *修改日期：  2018-06-12 15:16.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class DCUPortDataPkgParser_P2 {
    
    private DigitSignalDataParser digitSignalDataParser = new DigitSignalDataParser();
    
    public boolean Unpack(DCUCollectData collectData, String dtuID, DcuDataPkgInfo dcuDataPkgInfo, byte[] dtuData, int offset) {
        //端口采集数据包解包
        doUnpack(collectData, dtuData, offset);
        
        return true;
    }
    
    
    /**
     * 冰心数据包解包函数
     *
     * @param collectData 采集数据包实体类
     * @param dtuData DTU数据包
     * @param offset 偏移量
     */
    private  void doUnpack(DCUCollectData collectData, byte[] dtuData, int offset) {
        int unpackedBytes = 0;
        //解包打包ID和采集时间
        collectData.setPkgID(BitCoverter.toUint16(dtuData, offset + unpackedBytes));
        unpackedBytes += SizeOf.INT_16;//移动偏移量
        collectData.setCollectTimestamp(BitCoverter.toUint64(dtuData, offset + unpackedBytes));
        unpackedBytes += SizeOf.INT_64;
        
        //读取端口种类个数
        int portTypeNum = dtuData[offset + unpackedBytes];
        unpackedBytes += SizeOf.INT_8;
        
        for (int i = 0; i < portTypeNum; i++) {
            //端口的类型,比如F,B
            byte portType = dtuData[offset + unpackedBytes];
            unpackedBytes += SizeOf.INT_8;
            
            //获取端口的开始和结束索引号(临时版本就按照全部打包)
            int first_port = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
            unpackedBytes += SizeOf.INT_16;
            int last_port = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
            unpackedBytes += SizeOf.INT_16;
            
            unpackedBytes += unpackPortData(collectData.getData(), collectData.getCollectTimestamp(), portType, first_port, last_port, dtuData, offset + unpackedBytes);
            
        }
        
    }
    
    /**
     * 解包冰心端口数据
     *
     * @param portData    端口数据存放集合
     * @param collectTime 采集时间
     * @param portType    端口类型
     * @param first_port  起始端口号
     * @param last_port   最后端口号
     * @param dtuData     原始数据
     * @param offset      偏移量
     */
    private  int unpackPortData(HashMap<Byte, List<DCUPortData>> portData, long collectTime, byte portType, int first_port, int last_port, byte[] dtuData, int offset) {
        //声明变量
        int unpackedBytes = 0;
        List<DCUPortData> dcuPortData = new ArrayList<>();
        int portNum = first_port;
        
        if (portType == AcqPotyType.ACQ_PORT_TYPE_F) {
            for (; portNum <= last_port; portNum++) {
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                dcuPortData.add(new DCUPortData(portNum, (float) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            }
            portData.put(portType, dcuPortData);
        } else if (portType == AcqPotyType.ACQ_PORT_TYPE_B) {//开关报警类
            for (; portNum <= last_port; portNum++) {
                List<DigitSignalData> digitSignalDataList = new ArrayList<>();
                unpackedBytes += digitSignalDataParser.UnpackDigitSignalData(digitSignalDataList, collectTime, dtuData, offset + unpackedBytes);
                dcuPortData.add(new DCUPortData(portNum, digitSignalDataList));
            }
            portData.put(portType, dcuPortData);
        } else if (portType == AcqPotyType.ACQ_PORT_TYPE_R) {//电阻信号
            for (; portNum <= last_port; portNum++) {
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                dcuPortData.add(new DCUPortData(portNum, (float) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            }
            portData.put(portType, dcuPortData);
        } else if (portType == AcqPotyType.ACQ_PORT_TYPE_D) {//电能采集
            for (; portNum <= last_port; portNum++) {
                long data = BitCoverter.toUint64(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_64;
                dcuPortData.add(new DCUPortData(portNum, (double) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            }
            portData.put(portType, dcuPortData);
        } else if (portType == AcqPotyType.ACQ_PORT_TYPE_RF) {//RF类数据
            for (; portNum <= last_port; portNum++) {
                long data = BitCoverter.toUint32(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_32;
                dcuPortData.add(new DCUPortData(portNum, (double) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            }
            portData.put(portType, dcuPortData);
        } else if (portType == AcqPotyType.ACQ_PORT_TYPE_BT) {
            for (; portNum <= last_port; portNum++) {
                int data = BitCoverter.toUint16(dtuData, offset + unpackedBytes);
                unpackedBytes += SizeOf.INT_16;
                dcuPortData.add(new DCUPortData(portNum, (double) data / CommonConvention.FLOAT_AMPLIFY_FACTOR));
            }
            portData.put(portType, dcuPortData);
        }
        
        return unpackedBytes;
    }
}
