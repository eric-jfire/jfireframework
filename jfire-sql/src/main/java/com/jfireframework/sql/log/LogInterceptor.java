package com.jfireframework.sql.log;

public interface LogInterceptor
{
    public boolean isLogOn(String sql);
    
    public void log(String sql, Object... params);
}
