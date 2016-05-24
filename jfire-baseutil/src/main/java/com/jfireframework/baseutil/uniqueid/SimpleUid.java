package com.jfireframework.baseutil.uniqueid;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.code.RandomString;

public class SimpleUid
{
    private final long          base;
    private final short         pid;
    private final byte[]        internal;
    private final AtomicInteger count     = new AtomicInteger(0);
    private final static int    countMask = 0x00ffffff;
    
    public SimpleUid()
    {
        try
        {
            String _pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            pid = Short.parseShort(_pid);
            // 用到2046-01-01需要的位数是30的bit
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            base = format.parse("2016-01-01").getTime();
            // 本机mac地址的hash 32个bit
            int _maxHash;
            try
            {
                _maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
            }
            catch (Exception e)
            {
                _maxHash = RandomString.randomString(5).hashCode();
            }
            internal = new byte[5];
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
    
    /**
     * 生成一个12字节的id，使用24个字母的长度的String来表示。 id生成规则为：
     * 1-4字节为当前时间减去2016-01-01的秒数。30年内够用 5-6字节为当前的进程的pid
     * 7-9字节为当前mac地址的hash值，如果取不到mac地址才随机一个数字的hash值。并且只取后三个字节
     * 10-12字节为自增数字，也就意味着1秒内能产生1600w个id
     * 
     * @return
     */
    public String generate()
    {
        return StringUtil.toHexString(generateBytes());
    }
    
    public byte[] generateBytes()
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
        return result;
    }
    
}
