package com.jfireframework.baseutil.reflect.copy;

import java.lang.reflect.Field;

import com.jfireframework.baseutil.reflect.ReflectUtil;

import sun.misc.Unsafe;

public abstract class CopyField
{
    private static final Unsafe unsafe = ReflectUtil.getUnsafe();
    protected long              srcOff;
    protected long              desOff;
    
    public CopyField(Field srcField, Field desField)
    {
        srcOff = unsafe.objectFieldOffset(srcField);
        desOff = unsafe.objectFieldOffset(desField);
    }
    
    public abstract void copy(Object src, Object des);
    
    public static CopyField build(Field srcField, Field desField)
    {
        Class<?> type = srcField.getType();
        if (type == int.class)
        {
            return new IntField(srcField, desField);
        }
        else if (type == long.class)
        {
            return new LongField(srcField, desField);
        }
        else if (type == boolean.class)
        {
            return new BooleanField(srcField, desField);
        }
        else if (type == float.class)
        {
            return new FloatField(srcField, desField);
        }
        else if (type == double.class)
        {
            return new DoubleField(srcField, desField);
        }
        else if (type == short.class)
        {
            return new ShortField(srcField, desField);
        }
        else if (type == byte.class)
        {
            return new ByteField(srcField, desField);
        }
        else if (type == char.class)
        {
            return new CharField(srcField, desField);
        }
        else
        {
            return new ObjectField(srcField, desField);
        }
        
    }
    
    static class IntField extends CopyField
    {
        
        public IntField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putInt(des, desOff, unsafe.getInt(src, srcOff));
        }
        
    }
    
    static class LongField extends CopyField
    {
        
        public LongField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putLong(des, desOff, unsafe.getLong(src, srcOff));
        }
        
    }
    
    static class ShortField extends CopyField
    {
        
        public ShortField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putShort(des, desOff, unsafe.getShort(src, srcOff));
        }
        
    }
    
    static class ByteField extends CopyField
    {
        
        public ByteField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putByte(des, desOff, unsafe.getByte(src, srcOff));
        }
    }
    
    static class BooleanField extends CopyField
    {
        
        public BooleanField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putBoolean(des, desOff, unsafe.getBoolean(src, srcOff));
        }
        
    }
    
    static class FloatField extends CopyField
    {
        
        public FloatField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putFloat(des, desOff, unsafe.getFloat(src, srcOff));
        }
        
    }
    
    static class CharField extends CopyField
    {
        
        public CharField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putChar(des, desOff, unsafe.getChar(src, srcOff));
        }
        
    }
    
    static class DoubleField extends CopyField
    {
        
        public DoubleField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putDouble(des, desOff, unsafe.getDouble(src, srcOff));
        }
        
    }
    
    static class ObjectField extends CopyField
    {
        
        public ObjectField(Field srcField, Field desField)
        {
            super(srcField, desField);
        }
        
        @Override
        public void copy(Object src, Object des)
        {
            unsafe.putObject(des, desOff, unsafe.getObject(src, srcOff));
        }
        
    }
}
