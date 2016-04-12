package com.jfireframework.dbunit.util;

import com.jfireframework.baseutil.collection.StringCache;

public class SqlUtil
{
    
    /**
     * 使用一个表名和列名数组构造一个插入数据的预编译sql语句
     * 
     * @param tableName
     * @param columnNames
     * @return
     */
    public static String insertSqlForTable(String[] rowNames, String tableName)
    {
        StringCache cache = new StringCache("insert into " + tableName + "( ");
        for (String columnName : rowNames)
        {
            cache.append(columnName + ",");
        }
        cache.deleteLast();
        cache.append(" ) values(");
        for (int i = 0; i < rowNames.length; i++)
        {
            cache.append("?,");
        }
        cache.deleteLast();
        cache.append(" )");
        return cache.toString();
    }
    
}
