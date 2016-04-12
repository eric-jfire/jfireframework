package com.jfireframework.sql.test;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.sql.util.MapBeanFactory;

public class MapbeanFactoryTest extends BaseTestSupport
{
    @Test
    public void test()
    {
        new MapBeanFactory();
        Set<String> set = new HashSet<>();
        set.add("dsasda");
        try
        {
            MapBeanFactory.build(set, null);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e instanceof RuntimeException);
        }
    }
}
