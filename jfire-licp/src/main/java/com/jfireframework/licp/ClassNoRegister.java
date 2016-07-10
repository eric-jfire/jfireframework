package com.jfireframework.licp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 对象类型序号注册中心。对象类型序号从2开始。
 * 
 * @author linbin
 *
 */
public class ClassNoRegister
{
    private Class<?>[] types  = new Class[300];
    private int        originCount;
    private int        nowCount;
    // length是实际数组长度-1，这样方便进行计算
    private int        length = 999;
    
    public ClassNoRegister()
    {
        List<Class<?>> tmp = new ArrayList<Class<?>>();
        tmp.add(Integer.class);
        tmp.add(Short.class);
        tmp.add(Byte.class);
        tmp.add(Float.class);
        tmp.add(Long.class);
        tmp.add(Character.class);
        tmp.add(Double.class);
        tmp.add(Boolean.class);
        tmp.add(String.class);
        tmp.add(Integer[].class);
        tmp.add(Short[].class);
        tmp.add(Byte[].class);
        tmp.add(Float[].class);
        tmp.add(Long[].class);
        tmp.add(Character[].class);
        tmp.add(Double[].class);
        tmp.add(Boolean[].class);
        tmp.add(String[].class);
        tmp.add(Date.class);
        tmp.add(java.sql.Date.class);
        tmp.add(Calendar.class);
        tmp.add(ArrayList.class);
        tmp.add(LinkedList.class);
        tmp.add(HashMap.class);
        tmp.add(boolean.class);
        tmp.add(int.class);
        tmp.add(short.class);
        tmp.add(char.class);
        tmp.add(long.class);
        tmp.add(byte.class);
        tmp.add(float.class);
        tmp.add(double.class);
        tmp.add(boolean[].class);
        tmp.add(int[].class);
        tmp.add(short[].class);
        tmp.add(char[].class);
        tmp.add(long[].class);
        tmp.add(byte[].class);
        tmp.add(float[].class);
        tmp.add(double[].class);
        int index = 0;
        for (Class<?> each : tmp)
        {
            types[index] = each;
            index++;
        }
        originCount = index;
        nowCount = index;
    }
    
    /**
     * 永久性的增加一个类型需要到fose中
     * 
     * @param type
     */
    public void register(Class<?> type)
    {
        if (registerTemporary(type) == 0)
        {
            originCount++;
        }
    }
    
    /**
     * 注册一个临时的类。如果该类已经注册过了，返回该类的序号。否则注册该类。并且返回0
     * 
     * @param type
     * @return
     */
    @SuppressWarnings("rawtypes")
    public int registerTemporary(Class<?> type)
    {
        if (nowCount >= length)
        {
            Class[] newTypes = new Class[length * 2 + 2];
            System.arraycopy(types, 0, newTypes, 0, nowCount);
            types = newTypes;
            length = newTypes.length;
        }
        for (int i = 0; i < nowCount; i++)
        {
            if (types[i] == type)
            {
                return i + 1;
            }
        }
        int index = nowCount;
        types[index] = type;
        nowCount++;
        return 0;
    }
    
    /**
     * 获取一个类型在类型注册中的顺序号,顺序号从1开始，如果不存在返回0.并且会将该类增加到系统中
     * 
     * @param type
     * @return
     */
    public int indexOf(Class<?> type)
    {
        
        for (int i = 0; i < nowCount; i++)
        {
            if (types[i] == type)
            {
                return i + 1;
            }
        }
        registerTemporary(type);
        return 0;
    }
    
    public Class<?> getType(int index)
    {
        return types[index - 1];
    }
    
    public void clear()
    {
        nowCount = originCount;
    }
}
