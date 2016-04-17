package com.jfireframework.baseutil;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.time.Timewatch;

public class BufferTest
{
	ByteBuf<?> buf;
	
	@Test
	public void test()
	{
		buf = HeapByteBufPool.getInstance().get(100);
		buf.writeFloat(5.236f);
		buf.writeString("asdasdasda");
		buf.put((byte) 0x12);
		buf.writeMutableLengthLong(2112314123l);
		buf.writeDouble(5.236659);
		buf.writeChar('d');
		buf.writeInt(5);
		buf.writeBoolean(true);
		buf.writeShort((short) 2312);
		assertEquals(5.236f, buf.readFloat(), 0.0001);
		assertEquals("asdasdasda", buf.readString());
		assertEquals(0x12, buf.get());
		assertEquals(2112314123l, buf.readMutableLengthLong());
		assertEquals(5.236659, buf.readDouble(), 0.000001);
		assertEquals('d', buf.readChar());
		assertEquals(5, buf.readInt());
		assertEquals(true, buf.readBoolean());
		assertEquals((short) 2312, buf.readShort());
		// assertEquals(56, buf.readInt(40));
		buf = DirectByteBufPool.getInstance().get(100);
		buf.writeFloat(5.236f);
		buf.writeMutableLengthLong(2112314123l);
		buf.writeDouble(5.236659);
		buf.writeChar('d');
		buf.writeInt(5);
		buf.writeBoolean(true);
		buf.writeShort((short) 2312);
		assertEquals(5.236f, buf.readFloat(), 0.0001);
		assertEquals(2112314123l, buf.readMutableLengthLong());
		assertEquals(5.236659, buf.readDouble(), 0.000001);
		assertEquals('d', buf.readChar());
		assertEquals(5, buf.readInt());
		assertEquals(true, buf.readBoolean());
		assertEquals((short) 2312, buf.readShort());
	}
	
	@Test
	public void test2()
	{
		ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
		buf.writeString("你好");
		assertEquals("你好", buf.readString());
		buf = HeapByteBufPool.getInstance().get(100);
		buf.writeString("你好");
		assertEquals("你好", buf.readString());
	}
	
	@Test
	public void speed()
	{
		ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
		int count = 10000000;
		Timewatch timewatch = new Timewatch();
		System.gc();
		timewatch.start();
		for (int i = 0; i < count; i++)
		{
			buf.clear().writeMutableLengthLong(100);
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
		}
		timewatch.end();
		long time1 = timewatch.getTotal();
		System.gc();
		Timewatch timewatch2 = new Timewatch();
		timewatch2.start();
		for (int i = 0; i < count; i++)
		{
			buf.clear().writeLong(100);
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
			buf.writeChar('c');
		}
		timewatch2.end();
		long time2 = timewatch2.getTotal();
		System.out.println("time1:" + time1);
		System.out.println("time2:" + time2);
	}
	
	@Test
	public void testLegnth()
	{
		ByteBuf<?> buf = HeapByteBufPool.getInstance().get(100);
		buf.writeLength(12);
		buf.writeLength(256);
		buf.writeLength(512);
		buf.writeLength(456895);
		buf.writeLength(465868618);
		assertEquals(12, buf.readLength());
		assertEquals(256, buf.readLength());
		assertEquals(512, buf.readLength());
		assertEquals(456895, buf.readLength());
		assertEquals(465868618, buf.readLength());
		buf = DirectByteBufPool.getInstance().get(100);
		buf.writeLength(12);
		buf.writeLength(256);
		buf.writeLength(512);
		buf.writeLength(456895);
		buf.writeLength(465868618);
		assertEquals(12, buf.readLength());
		assertEquals(256, buf.readLength());
		assertEquals(512, buf.readLength());
		assertEquals(456895, buf.readLength());
		assertEquals(465868618, buf.readLength());
	}
	
	@Test
	public void test6()
	{
		System.out.println(Runtime.getRuntime().freeMemory()/1024);
		for (int i = 0; i < 1000000; i++)
		{
			ByteBuf<?> buf = DirectByteBuf.allocate(1024);
			buf.release();
		}
		System.out.println(Runtime.getRuntime().freeMemory()/1024);
	}
}
