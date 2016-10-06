package com.jfireframework.sql.test;

import com.jfireframework.sql.log.LogInterceptor;

public class LogSql implements LogInterceptor
{
    
    @Override
    public boolean isLogOn(String sql)
    {
        return true;
    }
    
    @Override
    public void log(String sql, Object... params)
    {
        System.out.println(sql);
    }
    
}
