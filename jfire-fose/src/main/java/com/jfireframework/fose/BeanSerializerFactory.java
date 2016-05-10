package com.jfireframework.fose;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.fose.field.CacheField;
import com.jfireframework.fose.field.DirectObjectField;
import com.jfireframework.fose.serializer.ArrayListSerializer;
import com.jfireframework.fose.serializer.CalendarSerializer;
import com.jfireframework.fose.serializer.DateSerializer;
import com.jfireframework.fose.serializer.FieldContainerSerializer;
import com.jfireframework.fose.serializer.Serializer;
import com.jfireframework.fose.serializer.StringSerializer;
import com.jfireframework.fose.serializer.array.BooleanArraySerializer;
import com.jfireframework.fose.serializer.array.ByteArraySerializer;
import com.jfireframework.fose.serializer.array.CharArraySerializer;
import com.jfireframework.fose.serializer.array.DoubleArraySerializer;
import com.jfireframework.fose.serializer.array.FloatArraySerializer;
import com.jfireframework.fose.serializer.array.IntArraySerializer;
import com.jfireframework.fose.serializer.array.LongArraySerializer;
import com.jfireframework.fose.serializer.array.ObjectArraySerializer;
import com.jfireframework.fose.serializer.array.ShortArraySerializer;
import com.jfireframework.fose.util.FieldComparator;
import com.jfireframework.fose.util.FieldFactory;
import com.jfireframework.fose.util.FoseIgnore;

public class BeanSerializerFactory
{
    private static Map<Class<?>, Serializer> typeSerializerMap     = new HashMap<Class<?>, Serializer>();
    private static Map<String, Class<?>>     nameTypeMap           = new ConcurrentHashMap<String, Class<?>>();
    private static ObjectArraySerializer     objectArraySerializer = new ObjectArraySerializer();
    private static FieldComparator           fieldComparator       = new FieldComparator();
    static
    {
        nameTypeMap.put(String.class.getName(), String.class);
        nameTypeMap.put(HashMap.class.getName(), HashMap.class);
        nameTypeMap.put(Integer.class.getName(), Integer.class);
        nameTypeMap.put(ArrayList.class.getName(), ArrayList.class);
        nameTypeMap.put(Date.class.getName(), Date.class);
    }
    static
    {
        typeSerializerMap.put(Date.class, new DateSerializer());
        typeSerializerMap.put(Calendar.class, new CalendarSerializer());
        typeSerializerMap.put(String.class, new StringSerializer());
        typeSerializerMap.put(int.class, new IntArraySerializer());
        typeSerializerMap.put(short.class, new ShortArraySerializer());
        typeSerializerMap.put(byte.class, new ByteArraySerializer());
        typeSerializerMap.put(char.class, new CharArraySerializer());
        typeSerializerMap.put(boolean.class, new BooleanArraySerializer());
        typeSerializerMap.put(float.class, new FloatArraySerializer());
        typeSerializerMap.put(double.class, new DoubleArraySerializer());
        typeSerializerMap.put(long.class, new LongArraySerializer());
        typeSerializerMap.put(ArrayList.class, new ArrayListSerializer());
    }
    
    public static Class<?> getType(String className) throws ClassNotFoundException
    {
        Class<?> target = nameTypeMap.get(className);
        if (target == null)
        {
            target = Class.forName(className);
            nameTypeMap.put(className, target);
        }
        return target;
    }
    
    public static Serializer getSerializer(Class<?> type)
    {
        Serializer serializer = typeSerializerMap.get(type);
        if (serializer != null)
        {
            return serializer;
        }
        else
        {
            if (type.isArray())
            {
                Class<?> origin = type;
                while (type.isArray())
                {
                    type = type.getComponentType();
                }
                if (type.isPrimitive())
                {
                    typeSerializerMap.put(origin, typeSerializerMap.get(type));
                    return typeSerializerMap.get(type);
                }
                else
                {
                    typeSerializerMap.put(origin, objectArraySerializer);
                    return objectArraySerializer;
                }
            }
            analyse(type);
            return typeSerializerMap.get(type);
        }
    }
    
    private static synchronized void analyse(Class<?> type)
    {
        if (typeSerializerMap.containsKey(type) || type.equals(Object.class))
        {
            return;
        }
        CacheField[] cacheFields = getAllFields(type);
        int objectFieldCount = 0;
        for (CacheField each : cacheFields)
        {
            if (each instanceof DirectObjectField)
            {
                objectFieldCount++;
            }
        }
        DirectObjectField[] objectFields = new DirectObjectField[objectFieldCount];
        for (CacheField each : cacheFields)
        {
            if (each instanceof DirectObjectField)
            {
                objectFields[--objectFieldCount] = (DirectObjectField) each;
            }
        }
        FieldContainerSerializer beanSerializer = new FieldContainerSerializer();
        Arrays.sort(cacheFields, fieldComparator);
        beanSerializer.setCacheFields(cacheFields);
        beanSerializer.setObjectFields(objectFields);
        typeSerializerMap.put(type, beanSerializer);
        nameTypeMap.put(type.getName(), type);
        for (DirectObjectField each : objectFields)
        {
            analyse(each.getRootType());
        }
    }
    
    /**
     * 获取该类的所有field,不包括静态field
     * 
     * @param src
     * @return
     */
    private static CacheField[] getAllFields(Class<?> src)
    {
        ArrayList<CacheField> tmp = new ArrayList<CacheField>();
        while (src != Object.class && src != null)
        {
            for (Field each : src.getDeclaredFields())
            {
                int modi = each.getModifiers();
                if (Modifier.isStatic(modi) || each.isAnnotationPresent(FoseIgnore.class))
                {
                    continue;
                }
                CacheField cacheField = FieldFactory.buildCacheField(each);
                tmp.add(cacheField);
            }
            src = src.getSuperclass();
        }
        return tmp.toArray(new CacheField[0]);
    }
    
}
