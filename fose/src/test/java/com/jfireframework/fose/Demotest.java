package com.jfireframework.fose;

import java.lang.reflect.Array;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.fose.Fose;

public class Demotest
{
	private Logger logger = ConsoleLogFactory.getLogger();
	
	@Test
	public void test()
	{
		Timewatch timewatch = new Timewatch();
		int count = 300000;
		int length = 3;
		Class<?> compType = int[][].class;
		timewatch.start();
		for (int i = 0; i < count; i++)
		{
			Array.newInstance(compType, length);
		}
		timewatch.end();
		logger.info("已知低维类型，反射耗时:{}", timewatch.getTotal());
		timewatch.start();
		int[] dim = new int[] { 3, 0, 0 };
		for (int i = 0; i < count; i++)
		{
			Array.newInstance(int.class, dim);
		}
		timewatch.end();
		logger.info("已知根类型，反射耗时:{}", timewatch.getTotal());
		
	}
	
	@Test
	public void test2()
	{
		Fose fose = new Fose();
		ByteBuf<?> buf = HeapByteBufPool.getInstance().get(100);
		fose.serialize("1234".substring(1, 2), buf);
		String result = (String) fose.deserialize(buf);
		System.out.println(result);
	}
}
