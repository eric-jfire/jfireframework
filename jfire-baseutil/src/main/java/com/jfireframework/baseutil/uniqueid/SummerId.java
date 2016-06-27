package com.jfireframework.baseutil.uniqueid;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;

public class SummerId implements Uid
{
    
    private final AtomicInteger count     = new AtomicInteger(0);
    private final static int    countMask = 0x00ffffff;
    private final byte          workedId;
    
    public SummerId(int workerId)
    {
        if (workerId >= 0 && workerId <= 255)
        {
            testLimit();
            this.workedId = (byte) (workerId & 0xff);
        }
        else
        {
            throw new UnSupportException("workerid的取值范围为0-255");
        }
    }
    
    private void testLimit()
    {
        AtomicInteger count = new AtomicInteger(0);
        long base = System.currentTimeMillis();
        while (System.currentTimeMillis() == base)
        {
            count.incrementAndGet();
        }
        if (count.get() >= 0x00ffffff)
        {
            throw new UnSupportException("当前服务器可以在一毫秒内产生太多id，超出了算法计算能力，不能使用该算法");
        }
    }
    
    @Override
    public String generate()
    {
        return StringUtil.toHexString(generateBytes());
    }
    
    public long generateLong()
    {
        byte[] result = generateBytes();
        long tmp = ((long) result[0] & 0xff) << 56;
        tmp |= ((long) result[1] & 0xff) << 48;
        tmp |= ((long) result[2] & 0xff) << 40;
        tmp |= ((long) result[3] & 0xff) << 32;
        tmp |= ((long) result[4] & 0xff) << 24;
        tmp |= ((long) result[5] & 0xff) << 16;
        tmp |= ((long) result[6] & 0xff) << 8;
        tmp |= ((long) result[7] & 0xff);
        return tmp;
    }
    
    @Override
    public String generateDigits()
    {
        long tmp = generateLong();
        char[] value = new char[11];
        value[0] = ByteTool.toDigit((int) ((tmp >>> 58) & short_mask));
        value[1] = ByteTool.toDigit((int) ((tmp >>> 52) & short_mask));
        value[2] = ByteTool.toDigit((int) ((tmp >>> 46) & short_mask));
        value[3] = ByteTool.toDigit((int) ((tmp >>> 40) & short_mask));
        value[4] = ByteTool.toDigit((int) ((tmp >>> 34) & short_mask));
        value[5] = ByteTool.toDigit((int) ((tmp >>> 28) & short_mask));
        value[6] = ByteTool.toDigit((int) ((tmp >>> 22) & short_mask));
        value[7] = ByteTool.toDigit((int) ((tmp >>> 16) & short_mask));
        value[8] = ByteTool.toDigit((int) ((tmp >>> 10) & short_mask));
        value[9] = ByteTool.toDigit((int) ((tmp >>> 4) & short_mask));
        value[10] = ByteTool.toDigit((int) ((tmp) & 0x000000000000000f));
        return String.valueOf(value);
    }
    
    /**
     * 使用64个bit进行id生成
     * 第一个bit不使用，默认为0
     * 2-32bit是为毫秒是时间戳。足够使用30年
     * 33-40bit是workerid的值
     * 41-64bit为序号，最大长度为16777215。
     * 该算法可以在1毫秒内产生16777215个id。
     * 注意：该算法未进行超时保护。如果机器的能力超过了1毫秒16777215的id，则id会出现重复。但是这个数字已经十分大。基本不太可能。
     */
    @Override
    public byte[] generateBytes()
    {
        byte[] result = new byte[8];
        long time = System.currentTimeMillis() - base;
        result[0] = (byte) (time >>> 24);
        result[1] = (byte) (time >>> 16);
        result[2] = (byte) (time >>> 8);
        result[3] = (byte) time;
        result[4] |= workedId;
        int tmp = count.getAndIncrement() & countMask;
        result[5] = (byte) (tmp >>> 16);
        result[6] = (byte) (tmp >>> 8);
        result[7] = (byte) (tmp);
        return result;
    }
    
}
