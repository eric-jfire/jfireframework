package com.jfireframework.sql.test;

import java.sql.SQLException;
import org.junit.Test;
import com.alibaba.druid.pool.DruidDataSource;
import com.jfireframework.sql.function.impl.SessionFactoryImpl;

public class CreateTableTest
{
    @Test
    public void test() throws SQLException
    {
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
            e.printStackTrace();
        }
        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);
        sessionFactory.setScanPackage("com.jfireframework.sql.test.table:out~com.jfireframework.sql.test.table.User3");
        sessionFactory.setTableMode("update");
        sessionFactory.init();
    }
}
