package com.jfireframework.sql.test;

import java.sql.SQLException;
import org.junit.Test;
import com.alibaba.druid.pool.DruidDataSource;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.sql.jfirecontext.MapperLoadFactory;
import com.jfireframework.sql.test.mappertest.MapperHolder;

public class MapperJfirecontextTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.sql.test");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("centerm");
        dataSource.setMaxActive(150);
        dataSource.setMaxWait(500);
        try
        {
            dataSource.getConnection();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MapperLoadFactory sessionFactory = new MapperLoadFactory();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setScanPackage("com.jfireframework.sql.test");
        sessionFactory.init();
        jfireContext.addSingletonEntity("sessionFactory", sessionFactory);
        jfireContext.initContext();
        MapperHolder holder = jfireContext.getBean(MapperHolder.class);
        holder.test();
    }
}
