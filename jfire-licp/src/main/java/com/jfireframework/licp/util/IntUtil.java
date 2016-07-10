package com.jfireframework.licp.util;

public class IntUtil
{
    /**
     * 将一个int进行zigzag编码
     * 
     * @param value
     * @return
     */
    public static final int zig(int value)
    {
        return (value << 1) ^ (value >> 31);
    }
    
    /**
     * 将一个int进行zigzag解码
     * 
     * @param value
     * @return
     */
    public static final int zag(int value)
    {
        return ((value >>> 1) ^ -(value & 1));
    }
}
