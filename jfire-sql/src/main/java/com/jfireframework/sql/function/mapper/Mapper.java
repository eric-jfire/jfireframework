package com.jfireframework.sql.function.mapper;

import javax.annotation.Resource;
import com.jfireframework.sql.function.SessionFactory;

/**
 * 用来给生成接口对象的类作为继承用
 * 方便在其中设置sqlSession
 * 
 * @author linbin
 * 
 */
public abstract class Mapper
{
    @Resource
    protected SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
    
}
