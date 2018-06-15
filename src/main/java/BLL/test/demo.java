package BLL.test;


import BLL.util.BitCoverter;

/**********************************************
 *
 //Copyright© 2014 冷云能源科技有限公司.版权所有
 *
 *文件名  ：  DataCenterTest.java
 *文件描述：  demo
 *修改日期：  2018-06-06 16:01.
 *文件作者：  Arike.Y 
 *
 **********************************************/

public class demo {
    public static void main(String[] args) {
        byte b =(byte)151;
        System.out.println(b);
    
    }
    
    
    private static byte[] longToByteArray(long l) {
        byte[] retVal = new byte[8];
        
        for (int i = 0; i < 8; i++) {
            retVal[i] = (byte) l;
            l >>= 8;
        }
        
        return retVal;
    }
}

