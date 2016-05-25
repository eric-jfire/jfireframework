package com.jfireframework.baseutil.uniqueid;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;

public class ShortUid
{
    private final long          base;
    private final AtomicInteger count      = new AtomicInteger(0);
    private final static int    countMask  = 0x000007ff;
    private final byte          coreId;
    private static final char[] digits     = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',                                     //
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',   //
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',   //
            '~', '!' };
    private static final long   short_mask = 0x000000000000003f;
    
    public ShortUid(int value)
    {
        try
        {
            if (value >= 0 && value < 32)
            {
                coreId = (byte) value;
            }
            else
            {
                throw new UnSupportException("间隔id的值在0到15之间");
            }
            // 用到2046-01-01需要的位数是30的bit
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            base = format.parse("2016-01-01").getTime();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 生成一个6字节的id，使用12个字母的长度的String来表示。
     * id生成规则为：
     * 1-30bit为当前时间减去2016-01-01的秒数,30年内够用 .
     * 31-35bit为初始化给定的参数。只有0-15这16个值。这意味着这个算法可以同时运行在16个逻辑节点上不碰撞
     * 36-48bit为自增数字，这意味着1秒内能产生8192个id.
     * 
     * @return
     */
    public String generate()
    {
        return StringUtil.toHexString(generateBytes());
    }
    
    public String generateShort()
    {
        byte[] result = generateBytes();
        long tmp = result[0] << 56;
        tmp |= ((long) result[1] & 0xff) << 48;
        tmp |= ((long) result[2] & 0xff) << 40;
        tmp |= ((long) result[3] & 0xff) << 32;
        tmp |= ((long) result[4] & 0xff) << 24;
        tmp |= ((long) result[5] & 0xff) << 16;
        char[] value = new char[8];
        value[0] = digits[(int) ((tmp >>> 58) & short_mask)];
        value[1] = digits[(int) ((tmp >>> 52) & short_mask)];
        value[2] = digits[(int) ((tmp >>> 46) & short_mask)];
        value[3] = digits[(int) ((tmp >>> 40) & short_mask)];
        value[4] = digits[(int) ((tmp >>> 34) & short_mask)];
        value[5] = digits[(int) ((tmp >>> 28) & short_mask)];
        value[6] = digits[(int) ((tmp >>> 22) & short_mask)];
        value[7] = digits[(int) ((tmp >>> 16) & short_mask)];
        return String.valueOf(value);
    }
    
    public byte[] generateBytes()
    {
        byte[] result = new byte[6];
        int time = (int) ((System.currentTimeMillis() - base) / 1000);
        result[0] = (byte) (time >>> 22);
        result[1] = (byte) (time >>> 14);
        result[2] = (byte) (time >>> 6);
        result[3] = (byte) ((time & 0xff) << 2);
        result[3] |= (byte) (coreId >>> 3);
        int tmp = count.getAndIncrement() & countMask;
        result[4] = (byte) ((coreId & 0xff) << 5);
        result[4] |= (byte) (tmp >>> 8);
        result[5] = (byte) tmp;
        return result;
    }
    
}
