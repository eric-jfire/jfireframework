package com.jfireframework.sql.util;

import javax.annotation.Resource;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.tx.TransactionManager;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;

@Resource
public class TxManager implements TransactionManager
{
    @Resource
    private SessionFactory sessionFactory;
    private Logger         logger = ConsoleLogFactory.getLogger();
                                  
    @Override
    public void beginTransAction()
    {
        logger.trace("事务开启");
        SqlSession session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            session = sessionFactory.openSession();
            sessionFactory.setCurrentSession(session);
        }
        session.beginTransAction();
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
        session.close();
    }
    
    @Override
    public void rollback()
    {
        logger.trace("事务回滚");
        SqlSession session = sessionFactory.getCurrentSession();
        session.rollback();
        session.close();
    }
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
}
