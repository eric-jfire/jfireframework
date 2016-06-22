package com.jfireframework.litl.format;

import java.util.concurrent.ConcurrentHashMap;

public class NameFormatRegister
{
    private static ConcurrentHashMap<String, Class<? extends Format>> formatMap = new ConcurrentHashMap<String, Class<? extends Format>>();
    static
    {
        
    }
    
    public static void register(String name, Class<? extends Format> type)
    {
        formatMap.put(name, type);
    }
    
    public static ConcurrentHashMap<String, Class<? extends Format>> getFormats()
    {
        return formatMap;
    }
}
