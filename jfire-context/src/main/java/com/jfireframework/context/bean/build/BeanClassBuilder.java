package com.jfireframework.context.bean.build;

import com.jfireframework.context.JfireContext;
import com.jfireframework.context.bean.Bean;

public interface BeanClassBuilder
{
    public Bean build(Class<?> origin, JfireContext jfireContext);
    
    public void setInitArgument(String arg);
}
