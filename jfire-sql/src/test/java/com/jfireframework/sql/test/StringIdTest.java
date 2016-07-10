package com.jfireframework.sql.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.jfireframework.sql.test.entity.StringIdUser;

public class StringIdTest extends BaseTestSupport
{
    
    @Before
    public void before()
    {
        session = sessionFactory.getCurrentSession();
        if (session == null)
        {
            session = sessionFactory.openSession();
        }
        connection = session.getConnection();
    }
    
    @Test
    public void test() throws SQLException
    {
        connection.prepareStatement("delete from stringiduser").execute();
        StringIdUser user = new StringIdUser();
        user.setName("你好");
        session.save(user);
        try
        {
            ResultSet resultSet = connection.prepareStatement("select count(1) from stringiduser where name = '你好'").executeQuery();
            resultSet.next();
            int result = resultSet.getInt(1);
            Assert.assertEquals(1, result);
        }
        catch (Exception e)
        {
            Assert.fail();
        }
        
    }
}
