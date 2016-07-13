package com.jfireframework.licp.serializer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import com.jfireframework.licp.Licp;
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
import com.jfireframework.licp.serializer.base.BooleanSerializer;
import com.jfireframework.licp.serializer.base.ByteSerializer;
import com.jfireframework.licp.serializer.base.CharSerializer;
import com.jfireframework.licp.serializer.base.DoubleSerializer;
import com.jfireframework.licp.serializer.base.FloatSerializer;
import com.jfireframework.licp.serializer.base.IntegerSerializer;
import com.jfireframework.licp.serializer.base.LongSerializer;
import com.jfireframework.licp.serializer.base.ShortSerializer;
import com.jfireframework.licp.serializer.base.StringSerializer;
import com.jfireframework.licp.serializer.extra.ArraylistSerializer;
import com.jfireframework.licp.serializer.extra.CalendarSerializer;
import com.jfireframework.licp.serializer.extra.DateSerializer;
import com.jfireframework.licp.serializer.extra.HashMapSerializer;
import com.jfireframework.licp.serializer.extra.HashSetSerializer;
import com.jfireframework.licp.serializer.extra.LinkedListSerializer;

public class SerializerFactory
{
    private final HashMap<Class<?>, LicpSerializer> serializerMap = new HashMap<Class<?>, LicpSerializer>();
    
    public SerializerFactory()
    {
        serializerMap.put(int[].class, new IntArraySerializer());
        serializerMap.put(byte[].class, new ByteArraySerializer());
        serializerMap.put(short[].class, new ShortArraySerializer());
        serializerMap.put(long[].class, new LongArraySerializer());
        serializerMap.put(char[].class, new CharArraySerializer());
        serializerMap.put(boolean[].class, new BooleanArraySerializer());
        serializerMap.put(float[].class, new FloatArraySerializer());
        serializerMap.put(double[].class, new DoubleArraySerializer());
        /**************************/
        serializerMap.put(Integer.class, new IntegerSerializer());
        serializerMap.put(Boolean.class, new BooleanSerializer());
        serializerMap.put(Character.class, new CharSerializer());
        serializerMap.put(Short.class, new ShortSerializer());
        serializerMap.put(Byte.class, new ByteSerializer());
        serializerMap.put(Long.class, new LongSerializer());
        serializerMap.put(Float.class, new FloatSerializer());
        serializerMap.put(Double.class, new DoubleSerializer());
        serializerMap.put(String.class, new StringSerializer());
        /**************************/
        serializerMap.put(String[].class, new StringArraySerializer());
        serializerMap.put(Integer[].class, new IntegerArraySerializer());
        serializerMap.put(Long[].class, new WLongArraySerializer());
        serializerMap.put(Short[].class, new WShortArraySerializer());
        serializerMap.put(Byte[].class, new WByteArraySerializer());
        serializerMap.put(Character[].class, new WCharArraySerializer());
        serializerMap.put(Float[].class, new WFloatArraySerializer());
        serializerMap.put(Double[].class, new WDoubleArraySerializer());
        serializerMap.put(Boolean[].class, new WBooleanArraySerializer());
        /**************************/
        serializerMap.put(Date.class, new DateSerializer(false));
        serializerMap.put(java.sql.Date.class, new DateSerializer(true));
        serializerMap.put(Calendar.class, new CalendarSerializer());
        serializerMap.put(ArrayList.class, new ArraylistSerializer());
        serializerMap.put(LinkedList.class, new LinkedListSerializer());
        serializerMap.put(HashMap.class, new HashMapSerializer());
        serializerMap.put(HashSet.class, new HashSetSerializer());
    }
    
    public void register(Class<?> type, LicpSerializer serializer)
    {
        serializerMap.put(type, serializer);
    }
    
    public LicpSerializer get(Class<?> type, Licp licp)
    {
        LicpSerializer serializer = serializerMap.get(type);
        if (serializer != null)
        {
            return serializer;
        }
        if (type.isArray())
        {
            serializer = new ObjectArraySerializer(type, licp);
        }
        else
        {
            serializer = new ObjectSerializer(type, licp);
        }
        serializerMap.put(type, serializer);
        return serializer;
        
    }
}
