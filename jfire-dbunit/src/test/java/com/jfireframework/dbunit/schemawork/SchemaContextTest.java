package com.jfireframework.dbunit.schemawork;

import org.junit.Test;
import com.jfireframework.dbunit.schema.DbUnit;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class SchemaContextTest
{
    @Test
    public void test()
    {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/thirdpay");
        dataSource.setUser("root");
        dataSource.setPassword("centerm");
        DbUnit unit = new DbUnit(1, dataSource);
        unit.exportExcelWithData();
        unit.importExcelFile(null);
    }
}
