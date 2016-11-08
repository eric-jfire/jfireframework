package com.jfireframework.sql.extra.interceptor;

public interface SqlPreInterceptor
{
    public String preIntercept(String sql, Object... params);
}
