package com.jfireframework.baseutil.simplelog;

public interface Logger
{
    public void info(String msg, Object... params);
    
    public void warn(String msg, Object... params);
    
    public void debug(String msg, Object... params);
    
    public void trace(String msg, Object... params);
    
    public void error(String msg, Object... params);
    
    public boolean isTraceEnabled();
    
    public boolean isDebugEnabled();
}
