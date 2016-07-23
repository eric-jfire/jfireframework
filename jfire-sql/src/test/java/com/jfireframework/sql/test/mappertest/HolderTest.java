package com.jfireframework.sql.test.mappertest;

import java.sql.SQLException;
import org.junit.Test;
import com.alibaba.druid.pool.DruidDataSource;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.dbunit.schema.DbUnit;
import com.jfireframework.sql.function.impl.SessionFactoryImpl;

public class HolderTest
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
        jfireContext.addSingletonEntity("dataSource", dataSource);
        jfireContext.addBean(SessionFactoryImpl.class);
        BeanConfig beanConfig = new BeanConfig("com.jfireframework.sql.function.impl.SessionFactoryImpl");
        beanConfig.putParam("scanPackage", "com.jfireframework.sql.test");
        jfireContext.addBeanConfig(beanConfig);
        DbUnit testUnit = new DbUnit(DbUnit.SAVE_IN_MEM, dataSource);
        testUnit.clearSchemaData();
        testUnit.importExcelFile();
        MapperHolder holder = jfireContext.getBean(MapperHolder.class);
        holder.test();
        SingletonMapper mapper = jfireContext.getBean(SingletonMapper.class);
        mapper.test();
    }
    
}
