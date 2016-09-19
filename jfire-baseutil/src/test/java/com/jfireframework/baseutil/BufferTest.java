package com.jfireframework.baseutil;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;

public class BufferTest
{
    ByteBuf<?> buf;
    
    @Test
    public void test()
    {
        buf = DirectByteBuf.allocate(100);
        buf.writeFloat(5.236f);
        buf.put((byte) 0x12);
        buf.writeDouble(5.236659);
        buf.writeChar('d');
        buf.writeInt(5);
        buf.writeBoolean(true);
        buf.writeShort((short) 2312);
        buf.writeString("abc");
        assertEquals(5.236f, buf.readFloat(), 0.0001);
        assertEquals(0x12, buf.get());
        assertEquals(5.236659, buf.readDouble(), 0.000001);
        assertEquals('d', buf.readChar());
        assertEquals(5, buf.readInt());
        assertEquals(true, buf.readBoolean());
        assertEquals((short) 2312, buf.readShort());
        assertEquals("abc", buf.readString());
        // assertEquals(56, buf.readInt(40));
        buf = DirectByteBufPool.getInstance().get(100);
        buf.writeFloat(5.236f);
        buf.writeDouble(5.236659);
        buf.writeChar('d');
        buf.writeInt(5);
        buf.writeBoolean(true);
        buf.writeShort((short) 2312);
        assertEquals(5.236f, buf.readFloat(), 0.0001);
        assertEquals(5.236659, buf.readDouble(), 0.000001);
        assertEquals('d', buf.readChar());
        assertEquals(5, buf.readInt());
        assertEquals(true, buf.readBoolean());
        assertEquals((short) 2312, buf.readShort());
    }
    
    @Test
    public void testLegnth()
    {
        ByteBuf<?> buf = HeapByteBufPool.getInstance().get(100);
        buf.writePositive(12);
        buf.writePositive(256);
        buf.writePositive(512);
        buf.writePositive(456895);
        buf.writePositive(465868618);
        assertEquals(12, buf.readPositive());
        assertEquals(256, buf.readPositive());
        assertEquals(512, buf.readPositive());
        assertEquals(456895, buf.readPositive());
        assertEquals(465868618, buf.readPositive());
        buf = DirectByteBufPool.getInstance().get(100);
        buf.writePositive(12);
        buf.writePositive(256);
        buf.writePositive(512);
        buf.writePositive(456895);
        buf.writePositive(465868618);
        assertEquals(12, buf.readPositive());
        assertEquals(256, buf.readPositive());
        assertEquals(512, buf.readPositive());
        assertEquals(456895, buf.readPositive());
        assertEquals(465868618, buf.readPositive());
    }
    
    @Test
    public void test6()
    {
        System.out.println(Runtime.getRuntime().freeMemory() / 1024);
        for (int i = 0; i < 1000000; i++)
        {
            ByteBuf<?> buf = DirectByteBuf.allocate(1024);
            buf.release();
        }
        System.out.println(Runtime.getRuntime().freeMemory() / 1024);
    }
}
