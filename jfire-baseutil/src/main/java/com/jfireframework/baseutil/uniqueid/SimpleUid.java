package com.jfireframework.baseutil.uniqueid;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.StringUtil;

public class SimpleUid
{
    private long             base;
    private short            pid;
    private byte[]           internal  = new byte[5];
    private AtomicInteger    count     = new AtomicInteger(0);
    private final static int countMask = 0x00ffffff;
    
    public SimpleUid()
    {
        try
        {
            String _pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            pid = Short.valueOf(_pid);
            // 用到2046-01-01需要的位数是30的bit
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            base = format.parse("2016-01-01").getTime();
            // 本机mac地址的hash 32个bit
            int _maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
            internal[0] = (byte) (pid >>> 8);
            internal[1] = (byte) pid;
            internal[2] = (byte) (_maxHash >>> 16);
            internal[3] = (byte) (_maxHash >>> 8);
            internal[4] = (byte) (_maxHash);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String id()
    {
        byte[] result = new byte[12];
        int time = (int) ((System.currentTimeMillis() - base) / 1000);
        result[0] = (byte) (time >>> 24);
        result[1] = (byte) (time >>> 16);
        result[2] = (byte) (time >>> 8);
        result[3] = (byte) time;
        result[4] = internal[0];
        result[5] = internal[1];
        result[6] = internal[2];
        result[7] = internal[3];
        result[8] = internal[4];
        int tmp = count.incrementAndGet() & countMask;
        result[9] = (byte) (tmp >>> 16);
        result[10] = (byte) (tmp >>> 8);
        result[11] = (byte) (tmp);
        return StringUtil.toHexString(result);
    }
    
}
