package BLL.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  回执类
 *修改日期：  2018-06-20 18:02.
 *文件作者：  Arike.Y 
 *
 **********************************************/

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class RecvDCUPortDataAck {
    private int DCUID;
    private Short DCUDataPkgID;
}
