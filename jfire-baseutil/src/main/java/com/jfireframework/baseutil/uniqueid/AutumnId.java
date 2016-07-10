package com.jfireframework.baseutil.uniqueid;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.StringUtil;

public class AutumnId implements Uid
{
    private static final short         pid;
    private static final byte[]        internal = new byte[5];
    private static final AtomicInteger count    = new AtomicInteger(0);
    private static volatile AutumnId   INSTANCE;
    
    private AutumnId()
    {
    }
    
    public static final AutumnId instance()
    {
        if (INSTANCE != null)
        {
            return INSTANCE;
        }
        synchronized (count)
        {
            if (INSTANCE != null)
            {
                return INSTANCE;
            }
            INSTANCE = new AutumnId();
            return INSTANCE;
        }
    }
    
    static
    {
        String _pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        pid = Short.valueOf(_pid);
        // 本机mac地址的hash 32个bit
        int _maxHash;
        try
        {
            _maxHash = StringUtil.toHexString(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress()).hashCode();
        }
        catch (Exception e)
        {
            _maxHash = new Random().nextInt();
        }
        internal[0] = (byte) (pid >>> 8);
        internal[1] = (byte) pid;
        internal[2] = (byte) (_maxHash >>> 16);
        internal[3] = (byte) (_maxHash >>> 8);
        internal[4] = (byte) (_maxHash);
    }
    
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
        int tmp = count.incrementAndGet();
        result[9] = (byte) (tmp >>> 24);
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
