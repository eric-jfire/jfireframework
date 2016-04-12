package com.jfireframework.dbunit.util;

public class Util
{
    /**
     * 返回虚拟机使用内存量的一个估计值,内容为"xxM"
     * 
     * @return
     */
    public static String usedMemory()
    {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "M";
    }
}
