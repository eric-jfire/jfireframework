package com.jfireframework.baseutil.copy.field.impl;

import java.lang.reflect.Field;

public class FloatField extends ObjectCopyField
{
    
    public FloatField(Field srcField, Field targetField)
    {
        super(srcField, targetField);
    }
    
    /**
     * object类型的就直接使用这个实现即可
     */
    @SuppressWarnings("restriction")
    public void copy(Object src, Object target)
    {
        unsafe.putFloat(target, targetOffset, unsafe.getFloat(src, srcOffset));
    }
}
