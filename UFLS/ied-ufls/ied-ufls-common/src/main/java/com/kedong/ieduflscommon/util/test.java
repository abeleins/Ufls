package com.kedong.ieduflscommon.util;

import com.kedong.ieduflscommon.enums.CityEnum;

import java.net.InetAddress;
import java.util.Properties;

/**
 * @Author: abel
 * @Date: 2022/12/31 10:23
 * @Description:
 */
public class test {
    public static void main(String[] args) throws Exception{
//        String hostName = System.getProperties().getProperty("hostName");
        String hostName1 = InetAddress.getLocalHost().getHostName();
        System.out.println(hostName1);
        System.out.println(CityEnum.getCity("LF").getRegionName());
    }
}
