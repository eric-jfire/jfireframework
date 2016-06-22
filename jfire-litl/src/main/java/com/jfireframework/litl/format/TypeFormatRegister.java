package com.jfireframework.litl.format;

import java.util.concurrent.ConcurrentHashMap;

public class TypeFormatRegister
{
    private static ConcurrentHashMap<Class<?>, Class<? extends Format>> formatMap = new ConcurrentHashMap<Class<?>, Class<? extends Format>>();
    
    public static void register(Class<?> type, Class<? extends Format> format)
    {
        formatMap.put(type, format);
    }
    
    public static Class<? extends Format> get(Class<?> type)
    {
        return formatMap.get(type);
    }
}
