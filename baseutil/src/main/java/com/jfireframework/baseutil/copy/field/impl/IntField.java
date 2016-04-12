package com.jfireframework.baseutil.copy.field.impl;

import java.lang.reflect.Field;

public class IntField extends ObjectCopyField
{
    public IntField(Field srcField, Field targetField)
    {
        super(srcField, targetField);
    }
    
    @SuppressWarnings("restriction")
    @Override
    public void copy(Object src, Object target)
    {
        unsafe.putInt(target, targetOffset, unsafe.getInt(src, srcOffset));
    }
    
}
