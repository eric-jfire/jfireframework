package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.Map;
import com.jfireframework.context.bean.Bean;

@SuppressWarnings("restriction")
public class InterfaceField extends AbstractDependencyField
{
    private Bean implBean;
    
    public InterfaceField(Field field, Bean implBean)
    {
        super(field);
        this.implBean = implBean;
    }
    
    @Override
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        unsafe.putObject(src, offset, implBean.getInstance(beanInstanceMap));
    }
    
}
