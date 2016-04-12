package com.jfireframework.baseutil.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CodeLocation
{
	/**
	 * 调用该方法所在的行数信息
	 * 
	 * @return
	 */
	public static String getCodeLocation()
	{
		return getCodeLocation(3);
	}
	
	/**
	 * 获取方法调用的信息.1代表调用这个方法所在的行,2代表再上一层
	 * 
	 * @param deep
	 * @return
	 */
	public static String getCodeLocation(int deep)
	{
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[deep];
		int index = stackTraceElement.getClassName().lastIndexOf(".") + 1;
		return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getClassName().substring(index) + ".java:" + stackTraceElement.getLineNumber() + ")";
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("sdasd");
		ExecutorService poo = Executors.newCachedThreadPool();
		poo.submit(new Runnable() {
			public void run()
			{
				System.out.println(CodeLocation.getCodeLocation());
				
			}
		});
		poo.shutdown();
		poo.awaitTermination(50, TimeUnit.SECONDS);
	}
}
