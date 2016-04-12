package com.jfireframework.baseutil;

public class Demo
{
	public static void main(String[] args)
	{
		System.out.println(0xff);
		System.out.println(0xffff);
		System.out.println(0xffffff);
		System.out.println(0x7fffffff);
		System.out.println(Integer.MAX_VALUE);
		byte b = (byte) 0xff;
		int a = b & 0xff;
		System.out.println(a);
		System.out.println((byte)0xff);
	}
}
