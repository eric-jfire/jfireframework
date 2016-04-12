package com.jfireframework.sql.function.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

public abstract class AbstractRouteDataSourceImpl implements DataSource
{
    
    private Map<Object, DataSource> sourceMap = new HashMap<Object, DataSource>();
    
    /**
     * 返回一个key的值，该值用来决定使用什么哪一个数据源
     * 
     * @return
     */
    protected abstract Object decideDataSource();
    
    @Override
    public Connection getConnection() throws SQLException
    {
        return sourceMap.get(decideDataSource()).getConnection();
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        return sourceMap.get(decideDataSource()).getConnection(username, password);
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        return sourceMap.get(decideDataSource()).getLogWriter();
    }
    
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException
    {
        sourceMap.get(decideDataSource()).setLogWriter(out);
    }
    
    @Override
    public void setLoginTimeout(int seconds) throws SQLException
    {
        sourceMap.get(decideDataSource()).setLoginTimeout(seconds);
    }
    
    @Override
    public int getLoginTimeout() throws SQLException
    {
        return sourceMap.get(decideDataSource()).getLoginTimeout();
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return sourceMap.get(decideDataSource()).getParentLogger();
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return sourceMap.get(decideDataSource()).unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return sourceMap.get(decideDataSource()).isWrapperFor(iface);
    }
    
}
