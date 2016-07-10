package com.jframework.licp.test.basetest;

import java.util.Date;
import org.junit.Test;
import com.jfireframework.baseutil.code.RandomString;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.licp.Licp;
import com.jframework.licp.test.basetest.data.Device;

public class PerTest
{
    private Logger     logger  = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    public static int  testSum = 100000000;
    private ByteBuf<?> buf     = HeapByteBufPool.getInstance().get(100);
    
    private Device Builder()
    {
        Device device = new Device();
        device.setActivationTime(new Date());
        device.setBound(true);
        device.setBuildVersion(1);
        device.setId(9876543210L);
        device.setIdfa("照片没问wqeqw");
        device.setImei("照片没wewqe问");
        device.setMac("照qw片没问");
        device.setMajorVersion(3);
        device.setMinorVersion(6);
        device.setOpenUdid(RandomString.randomString(48));
        device.setOs(3);
        device.setOsVersion("照qwqw片没问");
        device.setPromoPlatformCode(94000000);
        device.setUuid("照片没qq问");
        device.setSn(device.getOpenUdid() + "_" + device.getUuid());
        device.setUserId(1234567890L);
        return device;
    }
    
    @Test
    public void longTest()
    {
        
    }
    
    @Test
    public void test()
    {
        Device device = Builder();
        Licp context = new Licp();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < testSum; i++)
        {
            context.serialize(device, buf.clear());
        }
        long lbseCost = System.currentTimeMillis() - t0;
        logger.info("lbse序列化耗时：{}", lbseCost);
    }
    
    @Test
    public void test2()
    {
        Device device = Builder();
        Licp context = new Licp();
        context.serialize(device, buf);
        for (int i = 0; i < testSum; i++)
        {
            context.deserialize(buf);
            buf.readIndex(0);
        }
    }
}
