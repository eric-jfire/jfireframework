package com.jfireframework.fose;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
        if (addTemporaryClass(type))
        {
            originCount++;
        }
    }
    
    @SuppressWarnings("rawtypes")
    public boolean addTemporaryClass(Class<?> type)
    {
        if (nowCount >= length)
        {
            Class[] newTypes = new Class[length * 2 + 2];
            System.arraycopy(types, 0, newTypes, 0, nowCount);
            types = newTypes;
            length = newTypes.length;
        }
        for (Class<?> each : types)
        {
            if (each == type)
            {
                return false;
            }
        }
        types[nowCount] = type;
        nowCount++;
        return true;
    }
    
    /**
     * 获取一个类型在类型注册中的顺序号，如果不存在返回-1.并且会将该类增加到系统中
     * 
     * @param type
     * @return
     */
    public int getIndex(Class<?> type)
    {
        
        for (int i = 0; i < nowCount; i++)
        {
            if (types[i].equals(type))
            {
                return i + 1;
            }
        }
        addTemporaryClass(type);
        return -1;
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
