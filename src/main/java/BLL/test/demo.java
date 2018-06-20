package BLL.test;


import BLL.util.FixedQueue;

import java.util.Date;

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
        FixedQueue<Integer> queue = new FixedQueue<>(20);
        for(int i = 0; i < 40; i++) {
            queue.offer(i);
        }
        System.out.println(new Date());
        
    }
}
