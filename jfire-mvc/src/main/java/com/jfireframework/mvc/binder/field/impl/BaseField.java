package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Set;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;

public class BaseField extends AbstractBinderField
{
    private final Transfer transfer;
    
    public BaseField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
        transfer = TransferFactory.get(field.getType());
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        transfer.setValue(entity, offset, value);
    }
    
}
