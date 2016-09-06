package com.jfireframework.sql.jfirecontext;

import com.jfireframework.context.JfireContext;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.build.BeanClassBuilder;
import com.jfireframework.sql.util.MapperBuilder;

public class JfireMapperBuilder extends MapperBuilder implements BeanClassBuilder
{
    @Override
    public Bean build(Class<?> origin, JfireContext jfireContext)
    {
        Class<?> result = build(origin);
        return new Bean(origin.getSimpleName(), false, result, jfireContext);
    }
    
    @Override
    public void setInitArgument(String arg)
    {
        initMetas(arg);
    }
    
}
