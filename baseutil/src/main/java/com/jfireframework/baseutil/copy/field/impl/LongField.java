package com.jfireframework.baseutil.copy.field.impl;

import java.lang.reflect.Field;

public class LongField extends ObjectCopyField
{
    public LongField(Field srcField, Field targetField)
    {
        super(srcField, targetField);
    }
    
    @SuppressWarnings("restriction")
    public void copy(Object src, Object target)
    {
        unsafe.putLong(target, targetOffset, unsafe.getLong(src, srcOffset));
    }
}
