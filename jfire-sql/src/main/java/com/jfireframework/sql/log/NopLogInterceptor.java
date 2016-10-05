package com.jfireframework.sql.log;

public class NopLogInterceptor implements LogInterceptor
{
    
    @Override
    public boolean isLogOn(String sql)
    {
        return false;
    }
    
    @Override
    public void log(String sql, Object... params)
    {
        ;
    }
    
}
