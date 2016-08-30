package com.jfireframework.sql.jfirecontext;

import javax.annotation.Resource;
import com.jfireframework.context.bean.load.BeanLoadFactory;
import com.jfireframework.sql.function.impl.SessionFactoryImpl;

@Resource(name = "sessionFactory")
public class MapperLoadFactory extends SessionFactoryImpl implements BeanLoadFactory
{
    
    @SuppressWarnings("unchecked")
    @Override
    public <T, E extends T> E load(Class<T> ckass)
    {
        return (E) mappers.get(ckass);
    }
}
