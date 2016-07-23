package com.jfireframework.sql.test;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.sql.test.entity.User4;
import com.jfireframework.sql.util.TxManager;

public class TxManagerTest extends BaseTestSupport
{
    @Test
    public void test()
    {
        TxManager txManager = new TxManager();
        txManager.setSessionFactory(sessionFactory);
        txManager.buildCurrentSession();
        txManager.beginTransAction();
        User4 user4 = new User4();
        user4.setId(12);
        session.save(user4);
        txManager.rollback();
        Assert.assertNull(session.get(User4.class, 12));
        session.close();
        try
        {
            txManager.commit();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
        }
    }
}
