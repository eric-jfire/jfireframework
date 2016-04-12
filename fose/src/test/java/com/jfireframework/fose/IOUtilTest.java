package com.jfireframework.fose;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import com.jfire.test.rule.CustomRule;
import com.jfireframework.baseutil.collection.ByteCache;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.fose.util.IOUtil;

public class IOUtilTest
{
	@Rule
	public CustomRule rule = new CustomRule();
	
	@Test
	public void test4() throws InterruptedException
	{
		int[] params = new int[] { -2147483648, -1717986919, -1288490190, -858993461, -429496732, -3, 429496726, 858993455, 1288490184, 1717986913, 2147483642, Integer.MAX_VALUE };
		Timewatch timewatch = new Timewatch();
		for (int i = 0; i < params.length - 1; i++)
		{
			timewatch.start();
			System.out.print(params[i] + "到" + params[i + 1] + "  ");
			run(params[i], params[i + 1]);
			timewatch.end();
			System.out.println("耗时" + timewatch.getTotal() + ",使用内存是" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M");
		}
		run(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	@Test
	public void testSpeed()
	{
		int[] params = new int[] { -10000000, -10000, 10000, 10000000 };
		Timewatch timewatch = new Timewatch();
		for (int i = 0; i < params.length - 1; i++)
		{
			timewatch.start();
			System.out.print(params[i] + "到" + params[i + 1] + "  ");
			run(params[i], params[i + 1]);
			timewatch.end();
			System.out.println("耗时" + timewatch.getTotal() + ",使用内存是" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + "M");
		}
	}
	
	public void run(int start, int end)
	{
		ByteCache cache = new ByteCache();
		int result;
		for (int i = start; i < end; i++)
		{
			cache.clear();
			IOUtil.writeInt(i, cache);
			result = IOUtil.readInt(cache);
			Assert.assertEquals(i, result);
		}
		System.out.print("当前状态：" + cache.toString());
	}
	
	@Test
	public void testLength()
	{
		ByteCache cache = new ByteCache();
		IOUtil.writeInt(-234, cache);
		System.out.println(cache.getWriteIndex());
	}
	
	@Test
	public void testFloat()
	{
		runFloat(Float.MAX_VALUE);
		runFloat(Float.MIN_VALUE);
	}
	
	public void runFloat(float target)
	{
		ByteCache cache = new ByteCache();
		IOUtil.writeFloat(target, cache);
		float result = IOUtil.readFloat(cache);
		Assert.assertEquals(target, result, 0.0001);
	}
	
	@Test
	public void testDouble()
	{
		runDouble(Double.MAX_VALUE);
		runDouble(Double.MIN_VALUE);
	}
	
	public void runDouble(double target)
	{
		ByteCache cache = new ByteCache();
		IOUtil.writeDouble(target, cache);
		double result = IOUtil.readDouble(cache);
		Assert.assertEquals(target, result, 0.0001);
	}
}
