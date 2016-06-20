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
        map.put(String.class, new StringTrans());
        map.put(Integer.class, new IntegerTrans());
        map.put(Long.class, new WLongTrans());
        map.put(Short.class, new WShortTrans());
        map.put(Byte.class, new WByteTrans());
        map.put(Float.class, new WFloatTrans());
        map.put(Double.class, new WDoubleTrans());
        map.put(Character.class, new CharacterTrans());
        map.put(Boolean.class, new WBooleanTrans());
    }
    
    public static Transfer get(Class<?> type)
    {
        return map.get(type);
    }
    
    static class intTransfer implements Transfer
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
    
    static class IntegerTrans implements Transfer
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
    
    static class WByteTrans implements Transfer
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
    
    static class CharacterTrans implements Transfer
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
    
    static class WLongTrans implements Transfer
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
    
    static class WShortTrans implements Transfer
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
    
    static class WBooleanTrans implements Transfer
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
    
    static class WDoubleTrans implements Transfer
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
    
    static class WFloatTrans implements Transfer
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
    
    static class StringTrans implements Transfer
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
    
    static class longTransfer implements Transfer
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
    
    static class booleanTransfer implements Transfer
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
    
    static class doubleTransfer implements Transfer
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
    
    static class floatTransfer implements Transfer
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
    
    static class shortTrans implements Transfer
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
    
    static class charTrans implements Transfer
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
    
    static class byteTrans implements Transfer
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
}
