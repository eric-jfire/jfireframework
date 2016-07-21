package com.jfireframework.sql.test;

import org.junit.Test;
import com.jfireframework.sql.util.TxManager;

public class TxTest extends BaseTestSupport
{
    
    @Test
    public void test()
    {
        TxManager txManager = new TxManager();
        txManager.setSessionFactory(sessionFactory);
        txManager.buildCurrentSession();
        txManager.beginTransAction();
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.deleteUser(1);
        txManager.beginTransAction();
        userDAO.getUserByid(1);
        txManager.commit();
        txManager.commit();
        txManager.closeCurrentSession();
    }
    
}
