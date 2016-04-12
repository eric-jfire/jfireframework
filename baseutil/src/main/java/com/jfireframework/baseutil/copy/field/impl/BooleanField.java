package com.jfireframework.baseutil.copy.field.impl;

import java.lang.reflect.Field;

public class BooleanField extends ObjectCopyField
{
    
    public BooleanField(Field srcField, Field targetField)
    {
        super(srcField, targetField);
    }
    
    /**
     * object类型的就直接使用这个实现即可
     */
    @SuppressWarnings("restriction")
    public void copy(Object src, Object target)
    {
        unsafe.putBoolean(target, targetOffset, unsafe.getBoolean(src, srcOffset));
    }
}
