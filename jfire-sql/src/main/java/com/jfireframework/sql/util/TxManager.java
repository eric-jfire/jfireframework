package com.jfireframework.sql.util;

import javax.annotation.Resource;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.tx.TransactionManager;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;

@Resource
public class TxManager implements TransactionManager
{
    @Resource
    private SessionFactory sessionFactory;
    private Logger         logger = ConsoleLogFactory.getLogger();
    
    @Override
    public void beginTransAction(int isolate)
    {
        logger.trace("事务开启");
        SqlSession session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            throw new UnSupportException("准备开启事务但是没有session,请检查");
        }
        session.beginTransAction(isolate);
    }
    
    @Override
    public void commit()
    {
        logger.debug("提交事务");
        SqlSession session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            throw new RuntimeException("事务在提交时没有session,session可能于其他地方被清楚,请检查");
        }
        session.commit();
    }
    
    @Override
    public void rollback()
    {
        logger.trace("事务回滚");
        SqlSession session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            throw new UnSupportException("准备回滚事务但是没有session,请检查");
        }
        session.rollback();
    }
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void buildCurrentSession()
    {
        sessionFactory.getOrCreateCurrentSession();
    }
    
    @Override
    public void closeCurrentSession()
    {
        SqlSession session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            throw new UnSupportException("准备关闭但是没有session,请检查");
        }
        session.close();
    }
}
