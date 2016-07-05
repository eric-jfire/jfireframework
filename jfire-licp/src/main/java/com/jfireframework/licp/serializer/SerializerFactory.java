package com.jfireframework.licp.serializer;

import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory
{
    private static final ConcurrentHashMap<Class<?>, LicpSerializer> serializerMap = new ConcurrentHashMap<Class<?>, LicpSerializer>();
    
    public static LicpSerializer get(Class<?> type)
    {
        
    }
}
