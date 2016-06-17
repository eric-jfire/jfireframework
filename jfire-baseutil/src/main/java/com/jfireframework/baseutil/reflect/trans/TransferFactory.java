package com.jfireframework.baseutil.reflect.trans;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;

public class TransferFactory
{
    private static Map<Class<?>, Transfer> map = new HashMap<Class<?>, Transfer>();
    
    static
    {
        map.put(int.class, new intTransfer());
        map.put(long.class, new longTransfer());
        map.put(float.class, new floatTransfer());
        map.put(double.class, new doubleTransfer());
        map.put(boolean.class, new booleanTransfer());
        map.put(short.class, new shortTrans());
        map.put(byte.class, new byteTrans());
        map.put(char.class, new charTrans());
        map.put(Integer.class, new IntegerTrans());
        map.put(Long.class, new LongTrans());
        map.put(Short.class, new ShortTrans());
        map.put(Byte.class, new ByteTrans());
        map.put(Float.class, new FloatTrans());
        map.put(Double.class, new DoubleTrans());
        map.put(Character.class, new CharacterTrans());
        map.put(Boolean.class, new BooleanTrans());
        
    }
    
    public static Transfer get(Class<?> type)
    {
        return map.get(type);
    }
}

class IntegerTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Integer.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
    
}

class ByteTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Byte.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
    
}

class CharacterTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Character.valueOf(value.charAt(0));
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
    
}

class LongTrans implements Transfer
{
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Long.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
}

class ShortTrans implements Transfer
{
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Short.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
}

class BooleanTrans implements Transfer
{
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Boolean.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
}

class DoubleTrans implements Transfer
{
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Double.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
}

class FloatTrans implements Transfer
{
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Float.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
}

class StringTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return value;
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putObject(entity, offset, trans(value));
    }
    
}

class longTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Long.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putLong(entity, offset, Long.parseLong(value));
    }
    
}

class booleanTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Boolean.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putBoolean(entity, offset, Boolean.parseBoolean(value));
    }
    
}

class doubleTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Integer.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putDouble(entity, offset, Double.parseDouble(value));
    }
    
}

class intTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Integer.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putInt(entity, offset, Integer.parseInt(value));
    }
    
}

class floatTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Integer.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putFloat(entity, offset, Float.parseFloat(value));
    }
    
}

class shortTrans implements Transfer
{
    public Object trans(String value)
    {
        return Short.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putShort(entity, offset, Short.parseShort(value));
    }
}

class charTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Character.valueOf(value.charAt(0));
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putChar(entity, offset, value.charAt(0));
    }
}

class byteTrans implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Byte.valueOf(value);
    }
    
    @Override
    public void setValue(Object entity, long offset, String value)
    {
        unsafe.putByte(entity, offset, Byte.parseByte(value));
    }
}
