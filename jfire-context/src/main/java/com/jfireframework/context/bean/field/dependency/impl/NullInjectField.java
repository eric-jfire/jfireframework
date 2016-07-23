package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.Map;

public class NullInjectField extends AbstractDependencyField
{
    
    public NullInjectField(Field field)
    {
        super(field);
    }
    
    @Override
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        ;
    }
    
}
