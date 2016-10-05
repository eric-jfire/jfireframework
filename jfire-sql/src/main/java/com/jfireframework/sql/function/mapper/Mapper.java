package com.jfireframework.sql.function.mapper;

import javax.annotation.Resource;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.log.LogInterceptor;

/**
 * 用来给生成接口对象的类作为继承用 方便在其中设置sqlSession
 * 
 * @author linbin
 * 
 */
public abstract class Mapper
{
    @Resource
    protected SessionFactory sessionFactory;
    protected LogInterceptor log;
    
    public void setLog(LogInterceptor log)
    {
        this.log = log;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
    
}
