package com.jfireframework.baseutil;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;
import com.jfireframework.baseutil.exception.UnSupportException;
import sun.security.provider.VerificationProvider;

public class Demo
{
    
    public int read(ByteBuf<?> buf)
    {
        byte b = buf.get();
        if (b >= -120 && b <= 127)
        {
            return b;
        }
        switch (b)
        {
            case -121:
                return buf.get() & 0xff;
            case -122:
                return ((buf.get() & 0xff) << 8) | (buf.get() & 0xff);
            case -123:
                return ((buf.get() & 0xff) << 16) | ((buf.get() & 0xff) << 8) | (buf.get() & 0xff);
            case -124:
                return ((buf.get() & 0xff) << 24) | ((buf.get() & 0xff) << 16) | ((buf.get() & 0xff) << 8) | (buf.get() & 0xff);
            case -125:
                return ~(buf.get() & 0xff);
            case -126:
                return ~(((buf.get() & 0xff) << 8) | (buf.get() & 0xff));
            case -127:
                return ~(((buf.get() & 0xff) << 16) | ((buf.get() & 0xff) << 8) | (buf.get() & 0xff));
            case -128:
                return ~(((buf.get() & 0xff) << 24) | ((buf.get() & 0xff) << 16) | ((buf.get() & 0xff) << 8) | (buf.get() & 0xff));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    public void write(ByteBuf<?> buf, int i)
    {
        if (i >= -120 && i <= 127)
        {
            buf.put((byte) i);
            return;
        }
        if (i > 0)
        {
            if (i <= 0x000000ff)
            {
                buf.put((byte) -121).put((byte) i);
            }
            else if (i <= 0x0000ffff)
            {
                buf.put((byte) -122).put((byte) (i >>> 8)).put((byte) i);
            }
            else if (i <= 0x00ffffff)
            {
                buf.put((byte) -123).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            }
            else
            {
                buf.put((byte) -124).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            }
        }
        else
        {
            i = ~i;
            if (i <= 0x000000ff)
            {
                buf.put((byte) -125).put((byte) i);
            }
            else if (i <= 0x0000ffff)
            {
                buf.put((byte) -126).put((byte) (i >>> 8)).put((byte) i);
            }
            else if (i <= 0x00ffffff)
            {
                buf.put((byte) -127).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            }
            else
            {
                buf.put((byte) -128).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            }
        }
    }
    
    @Test
    public void test()
    {
        ByteBuf<?> buf = DirectByteBuf.allocate(100);
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++)
        {
            buf.clear();
            buf.writeVarint(i);
            Assert.assertEquals(i, buf.readVarint());
        }
        buf.clear();
        buf.writeVarint(Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, buf.readVarint());
    }
    
    @Test
    public void chartest()
    {
        int i = 0;
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++)
        {
            // buf.clear();
            buf.writeVarChar(c);
            Assert.assertEquals(c, buf.readVarChar());
        }
    }
}
