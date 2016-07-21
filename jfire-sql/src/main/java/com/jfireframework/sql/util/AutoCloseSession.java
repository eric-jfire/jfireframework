package com.jfireframework.sql.util;

import javax.annotation.Resource;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.tx.AutoCloseManager;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;

@Resource
public class AutoCloseSession implements AutoCloseManager
{
    @Resource
    private SessionFactory sessionFactory;
    private static Logger  logger = ConsoleLogFactory.getLogger();
                                  
    @Override
    public void close()
    {
        logger.trace("关闭当前session{}", sessionFactory.getCurrentSession());
        SqlSession session = sessionFactory.getCurrentSession();
        if (session != null)
        {
            session.close();
        }
    }
    
}
