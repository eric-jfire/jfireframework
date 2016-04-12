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
        dataSource.setPassword("Starnetsql1019");
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
        
        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);
        sessionFactory.setScanPackage("link.jfire.orm.table");
        sessionFactory.setDbType("MariaDB");
        sessionFactory.setTableMode("update");
        sessionFactory.init();
    }
}
