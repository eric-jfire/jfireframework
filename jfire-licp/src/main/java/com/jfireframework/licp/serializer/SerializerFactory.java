package com.jfireframework.licp.serializer;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.licp.serializer.array.BooleanArraySerializer;
import com.jfireframework.licp.serializer.array.ByteArraySerializer;
import com.jfireframework.licp.serializer.array.CharArraySerializer;
import com.jfireframework.licp.serializer.array.DoubleArraySerializer;
import com.jfireframework.licp.serializer.array.FloatArraySerializer;
import com.jfireframework.licp.serializer.array.IntArraySerializer;
import com.jfireframework.licp.serializer.array.IntegerArraySerializer;
import com.jfireframework.licp.serializer.array.LongArraySerializer;
import com.jfireframework.licp.serializer.array.ObjectArraySerializer;
import com.jfireframework.licp.serializer.array.ShortArraySerializer;
import com.jfireframework.licp.serializer.array.StringArraySerializer;
import com.jfireframework.licp.serializer.array.WBooleanArraySerializer;
import com.jfireframework.licp.serializer.array.WByteArraySerializer;
import com.jfireframework.licp.serializer.array.WCharArraySerializer;
import com.jfireframework.licp.serializer.array.WDoubleArraySerializer;
import com.jfireframework.licp.serializer.array.WFloatArraySerializer;
import com.jfireframework.licp.serializer.array.WLongArraySerializer;
import com.jfireframework.licp.serializer.array.WShortArraySerializer;
import com.jfireframework.licp.serializer.extra.BooleanSerializer;
import com.jfireframework.licp.serializer.extra.ByteSerializer;
import com.jfireframework.licp.serializer.extra.CharSerializer;
import com.jfireframework.licp.serializer.extra.DoubleSerializer;
import com.jfireframework.licp.serializer.extra.FloatSerializer;
import com.jfireframework.licp.serializer.extra.IntegerSerializer;
import com.jfireframework.licp.serializer.extra.LongSerializer;
import com.jfireframework.licp.serializer.extra.ShortSerializer;

public class SerializerFactory
{
    private static final ConcurrentHashMap<Class<?>, LicpSerializer> serializerMap = new ConcurrentHashMap<Class<?>, LicpSerializer>();
    
    static
    {
        serializerMap.put(int[].class, new IntArraySerializer());
        serializerMap.put(byte[].class, new ByteArraySerializer());
        serializerMap.put(short[].class, new ShortArraySerializer());
        serializerMap.put(long[].class, new LongArraySerializer());
        serializerMap.put(char[].class, new CharArraySerializer());
        serializerMap.put(boolean[].class, new BooleanArraySerializer());
        serializerMap.put(float[].class, new FloatArraySerializer());
        serializerMap.put(double[].class, new DoubleArraySerializer());
        serializerMap.put(String[].class, new StringArraySerializer());
        serializerMap.put(Integer[].class, new IntegerArraySerializer());
        serializerMap.put(Long[].class, new WLongArraySerializer());
        serializerMap.put(Short[].class, new WShortArraySerializer());
        serializerMap.put(Byte[].class, new WByteArraySerializer());
        serializerMap.put(Character[].class, new WCharArraySerializer());
        serializerMap.put(Float[].class, new WFloatArraySerializer());
        serializerMap.put(Double[].class, new WDoubleArraySerializer());
        serializerMap.put(Boolean[].class, new WBooleanArraySerializer());
        serializerMap.put(Integer.class, new IntegerSerializer());
        serializerMap.put(Boolean.class, new BooleanSerializer());
        serializerMap.put(Character.class, new CharSerializer());
        serializerMap.put(Short.class, new ShortSerializer());
        serializerMap.put(Byte.class, new ByteSerializer());
        serializerMap.put(Long.class, new LongSerializer());
        serializerMap.put(Float.class, new FloatSerializer());
        serializerMap.put(Double.class, new DoubleSerializer());
    }
    
    public static LicpSerializer get(Class<?> type)
    {
        LicpSerializer serializer = serializerMap.get(type);
        if (serializer != null)
        {
            return serializer;
        }
        if (type.isArray())
        {
            serializer = new ObjectArraySerializer(type);
        }
        else
        {
            serializer = new ObjectSerializer(type);
        }
        serializerMap.putIfAbsent(type, serializer);
        return serializer;
        
    }
}
