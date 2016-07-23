package com.jfireframework.sql.test.mappertest;

import javax.annotation.Resource;
import org.junit.Assert;
import com.jfireframework.sql.test.UserDAO;

@Resource
public class SingletonMapper
{
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserDAO userDAO2;
    
    public void test()
    {
        Assert.assertTrue(userDAO == userDAO2);
    }
}
