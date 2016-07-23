package com.jfireframework.sql.jfirecontext;

import javax.annotation.Resource;
import com.jfireframework.context.bean.BeanInstanceHolder;
import com.jfireframework.sql.function.SessionFactory;

public class SqlMapperHolder implements BeanInstanceHolder
{
    @Resource
    private SessionFactory sessionFactory;
    
    @Override
    public Object getObject()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
