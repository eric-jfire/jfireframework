package com.jfireframework.sql.test;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.sql.util.DaoFactory;

public class DaoFactoryTest extends BaseTestSupport
{
    @Test
    public void test()
    {
        Set<String> set = new HashSet<>();
        set.add("sdasdasd");
        try
        {
            DaoFactory.buildDaoBean(set, null);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getCause() instanceof ClassNotFoundException);
        }
    }
}
