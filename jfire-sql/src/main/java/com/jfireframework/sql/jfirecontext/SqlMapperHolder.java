package com.jfireframework.sql.jfirecontext;

import javax.annotation.Resource;
import com.jfireframework.context.bean.BeanInstanceHolder;
import com.jfireframework.sql.function.SessionFactory;

public class SqlMapperHolder implements BeanInstanceHolder
{
    @Resource
    private SessionFactory sessionFactory;
    private Class<?>       ckass;
    
    @Override
    public Object getObject()
    {
        return sessionFactory.getMapper(ckass);
    }
    
}
