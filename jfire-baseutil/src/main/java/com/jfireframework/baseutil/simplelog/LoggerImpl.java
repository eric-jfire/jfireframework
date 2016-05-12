package com.jfireframework.baseutil.simplelog;

import java.util.Arrays;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.code.CodeLocation;

public class LoggerImpl implements Logger
{
    private int               level;
    private String            name;
    private static final long startedTime = System.currentTimeMillis();
    private boolean           location;
                              
    public LoggerImpl(String name, boolean location)
    {
        this.name = name;
        this.location = location;
    }
    
    public void updateLevel(int level)
    {
        this.level = level;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public void info(String msg, Object... params)
    {
        if (level <= ConsoleLogFactory.INFO)
        {
            output("INFO", msg, params);
        }
    }
    
    private void output(String levelName, String msg, Object... params)
    {
        String head = StringUtil.format("{} {} {} :", levelName, (System.currentTimeMillis() - startedTime), Thread.currentThread().getName());
        if (params == null || params.length == 0)
        {
            if (location)
            {
                System.out.println(head + " " + msg + "    " + CodeLocation.getCodeLocation(4));
            }
            else
            {
                System.out.println(head + " " + msg);
            }
            return;
        }
        if (params[params.length - 1] instanceof Throwable)
        {
            if (location)
            {
                System.out.println(head + " " + StringUtil.format(msg, Arrays.copyOf(params, params.length - 1)) + "    " + CodeLocation.getCodeLocation(4));
            }
            else
            {
                System.out.println(head + " " + StringUtil.format(msg, Arrays.copyOf(params, params.length - 1)));
            }
            ((Throwable) params[params.length - 1]).printStackTrace();
        }
        else
        {
            if (location)
            {
                System.out.println(head + " " + StringUtil.format(msg, params) + "    " + CodeLocation.getCodeLocation(4));
            }
            else
            {
                System.out.println(head + " " + StringUtil.format(msg, params));
            }
        }
    }
    
    @Override
    public void warn(String msg, Object... params)
    {
        if (level <= ConsoleLogFactory.WARN)
        {
            output("WARN", msg, params);
        }
    }
    
    @Override
    public void debug(String msg, Object... params)
    {
        if (level <= ConsoleLogFactory.DEBUG)
        {
            output("DEBUG", msg, params);
        }
    }
    
    @Override
    public void trace(String msg, Object... params)
    {
        if (level <= ConsoleLogFactory.TRACE)
        {
            output("TRACE", msg, params);
        }
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
    }
    
    @Override
    public void error(String msg, Object... params)
    {
        if (level <= ConsoleLogFactory.ERROR)
        {
            output("ERROR", msg, params);
        }
    }
    
    @Override
    public boolean isTraceEnabled()
    {
        return level >= ConsoleLogFactory.TRACE;
    }
    
    @Override
    public boolean isDebugEnabled()
    {
        return level >= ConsoleLogFactory.DEBUG;
    }
    
    public static void main(String[] args)
    {
        Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.ERROR);
        logger.error("eeqewqweq", new RuntimeException("dadasd"));
    }
}
