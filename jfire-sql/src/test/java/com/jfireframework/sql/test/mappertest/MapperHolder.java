package com.jfireframework.sql.test.mappertest;

import static org.junit.Assert.assertEquals;
import java.util.List;
import javax.annotation.Resource;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.test.UserDAO;

@Resource
public class MapperHolder
{
    @Resource
    private UserDAO        userDAO;
    @Resource
    private SessionFactory sessionFactory;
    
    public void test()
    {
        sessionFactory.getOrCreateCurrentSession();
        List<String> list = userDAO.getUsernames();
        assertEquals("林斌", list.get(0));
        assertEquals("林曦", list.get(1));
        sessionFactory.getCurrentSession().close();
    }
}
