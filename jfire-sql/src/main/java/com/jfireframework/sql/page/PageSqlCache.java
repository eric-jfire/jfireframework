package com.jfireframework.sql.page;

import java.util.concurrent.ConcurrentHashMap;

public class PageSqlCache
{
    private static final ConcurrentHashMap<String, String> querySqlCache = new ConcurrentHashMap<String, String>();
    private static final ConcurrentHashMap<String, String> countSqlCache = new ConcurrentHashMap<String, String>();
    
    public static String getQuerySql(String originSql)
    {
        return querySqlCache.get(originSql);
    }
    
    public static String getCountSql(String originSql)
    {
        return countSqlCache.get(originSql);
    }
    
    public static void putQuerySql(String originSql, String querySql)
    {
        querySqlCache.put(originSql, querySql);
    }
    
    public static void putCountSql(String originSql, String countSql)
    {
        countSqlCache.put(originSql, countSql);
    }
}
