package com.jfireframework.baseutil.uniqueid;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.atomic.AtomicInteger;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;

public class WinterId implements Uid
{
    private short            pid;
    private byte[]           internal  = new byte[6];
    private AtomicInteger    count     = new AtomicInteger(0);
    private final static int countMask = 0x00ffffff;
    
    public WinterId(final byte businessId)
    {
        try
        {
            testLimit();
            String _pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            pid = Short.valueOf(_pid);
            // 本机mac地址的hash 32个bit
            int _maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
            internal[0] = (byte) (pid >>> 8);
            internal[1] = (byte) pid;
            internal[2] = (byte) (_maxHash >>> 16);
            internal[3] = (byte) (_maxHash >>> 8);
            internal[4] = (byte) (_maxHash);
            internal[5] = businessId;
        }
        catch (Exception e)
        {
            throw new UnSupportException("winterid init error", e);
        }
    }
    
    private void testLimit()
    {
        AtomicInteger count = new AtomicInteger(0);
        int i = 0;
        long base = System.currentTimeMillis();
        while (i < 16777215)
        {
            count.incrementAndGet();
            i += 1;
        }
        long result = System.currentTimeMillis();
        if (result == base)
        {
            throw new UnSupportException("当前服务器可以在一毫秒内产生太多id，超出了算法计算能力，不能使用该算法");
        }
    }
    
    /**
     * 生成算法一共使用13个字节。其中头四个字节是时间戳。
     * 接着6个字节为初始化固定字节，分别是线程pid2个字节，网卡hash3个字节，业务字段1个字节。
     * 接着3个字节是自动增长数，意味着可以在1毫秒最多生成16777215个id。
     */
    @Override
    public byte[] generateBytes()
    {
        byte[] result = new byte[13];
        long time = System.currentTimeMillis() - base;
        result[0] = (byte) (time >>> 24);
        result[1] = (byte) (time >>> 16);
        result[2] = (byte) (time >>> 8);
        result[3] = (byte) time;
        result[4] = internal[0];
        result[5] = internal[1];
        result[6] = internal[2];
        result[7] = internal[3];
        result[8] = internal[4];
        result[9] = internal[5];
        int tmp = count.incrementAndGet() & countMask;
        result[10] = (byte) (tmp >>> 16);
        result[11] = (byte) (tmp >>> 8);
        result[12] = (byte) (tmp);
        return result;
    }
    
    @Override
    public String generate()
    {
        return StringUtil.toHexString(generateBytes());
    }
    
    @Override
    public long generateLong()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String generateDigits()
    {
        byte[] result = generateBytes();
        long tmp = ((long) result[0] & 0xff) << 40;
        tmp |= ((long) result[1] & 0xff) << 32;
        tmp |= ((long) result[2] & 0xff) << 24;
        tmp |= ((long) result[3] & 0xff) << 16;
        tmp |= ((long) result[4] & 0xff) << 8;
        tmp |= ((long) result[5] & 0xff);
        long tmp2 = ((long) result[6] & 0xff) << 40;
        tmp2 |= ((long) result[7] & 0xff) << 32;
        tmp2 |= ((long) result[8] & 0xff) << 24;
        tmp2 |= ((long) result[9] & 0xff) << 16;
        tmp2 |= ((long) result[10] & 0xff) << 8;
        tmp2 |= ((long) result[11] & 0xff);
        char[] digs = new char[18];
        digs[0] = ByteTool.toDigit((int) ((tmp >>> 42) & short_mask));
        digs[1] = ByteTool.toDigit((int) ((tmp >>> 36) & short_mask));
        digs[2] = ByteTool.toDigit((int) ((tmp >>> 30) & short_mask));
        digs[3] = ByteTool.toDigit((int) ((tmp >>> 24) & short_mask));
        digs[4] = ByteTool.toDigit((int) ((tmp >>> 18) & short_mask));
        digs[5] = ByteTool.toDigit((int) ((tmp >>> 12) & short_mask));
        digs[6] = ByteTool.toDigit((int) ((tmp >>> 6) & short_mask));
        digs[7] = ByteTool.toDigit((int) ((tmp) & short_mask));
        digs[8] = ByteTool.toDigit((int) ((tmp2 >>> 42) & short_mask));
        digs[9] = ByteTool.toDigit((int) ((tmp2 >>> 36) & short_mask));
        digs[10] = ByteTool.toDigit((int) ((tmp2 >>> 30) & short_mask));
        digs[11] = ByteTool.toDigit((int) ((tmp2 >>> 24) & short_mask));
        digs[12] = ByteTool.toDigit((int) ((tmp2 >>> 18) & short_mask));
        digs[13] = ByteTool.toDigit((int) ((tmp2 >>> 12) & short_mask));
        digs[14] = ByteTool.toDigit((int) ((tmp2 >>> 6) & short_mask));
        digs[15] = ByteTool.toDigit((int) ((tmp2) & short_mask));
        digs[16] = ByteTool.toDigit((int) (((result[12]) >>> 2) & short_mask));
        digs[17] = ByteTool.toDigit((result[12] & 0x03));
        return new String(digs);
    }
    
}
