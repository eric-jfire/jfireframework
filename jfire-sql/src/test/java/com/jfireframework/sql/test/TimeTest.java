package com.jfireframework.sql.test;

import java.sql.SQLException;
import org.junit.Test;
import com.jfireframework.sql.function.impl.SessionFactoryImpl;
import com.zaxxer.hikari.HikariDataSource;

public class TimeTest
{
    @Test
    public void test()
    {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("centerm");
        dataSource.setMaximumPoolSize(150);
        dataSource.setConnectionTimeout(1500);
        try
        {
            dataSource.getConnection();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int count = 10000;
        long t0 = System.nanoTime();
        for (int i = 0; i < count; i++)
        {
            SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);
            sessionFactory.setScanPackage("com.jfireframework.sql.test");
            sessionFactory.init();
        }
        long t1 = System.nanoTime();
        System.out.println((t1 - t0) / 1000000);
    }
}
