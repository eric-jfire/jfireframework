package com.jfireframework.baseutil.simplelog;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsoleLogFactory
{
    private static Map<String, Integer> levelMap    = new HashMap<String, Integer>();
    private static Map<String, Boolean> locationMap = new HashMap<String, Boolean>();
    public final static int             TRACE       = 1;
    public final static int             DEBUG       = 2;
    public final static int             INFO        = 3;
    public final static int             WARN        = 4;
    public final static int             ERROR       = 5;
    private static int                  rootLevel   = ERROR;
    
    static
    {
        Properties properties = new Properties();
        try
        {
            properties.load(ConsoleLogFactory.class.getClassLoader().getResourceAsStream("log.properties"));
            Enumeration<?> enums = properties.propertyNames();
            while (enums.hasMoreElements())
            {
                String name = (String) enums.nextElement();
                String value = properties.getProperty(name);
                if (value.contains(","))
                {
                    if (value.split(",")[1].equals("true"))
                    {
                        locationMap.put(name, true);
                    }
                    else
                    {
                        locationMap.put(name, false);
                    }
                }
                else
                {
                    locationMap.put(name, true);
                }
                if (value.startsWith("error"))
                {
                    levelMap.put(name, ConsoleLogFactory.ERROR);
                }
                if (value.equals("warn"))
                {
                    levelMap.put(name, ConsoleLogFactory.WARN);
                }
                if (value.startsWith("info"))
                {
                    levelMap.put(name, ConsoleLogFactory.INFO);
                }
                if (value.equals("debug"))
                {
                    levelMap.put(name, ConsoleLogFactory.DEBUG);
                }
                if (value.equals("trace"))
                {
                    levelMap.put(name, ConsoleLogFactory.TRACE);
                }
            }
        }
        catch (Exception e)
        {
        }
        
    }
    
    public static void addLoggerCfg(String packageName, int level)
    {
        levelMap.put(packageName, level);
    }
    
    public static Logger getLogger()
    {
        String className = (new Throwable()).getStackTrace()[1].getClassName();
        for (String each : levelMap.keySet())
        {
            if (className.startsWith(each))
            {
                LoggerImpl loggerImpl = new LoggerImpl(className, locationMap.get(each) == null ? false : locationMap.get(each));
                loggerImpl.setLevel(levelMap.get(each));
                return loggerImpl;
            }
        }
        LoggerImpl loggerImpl = new LoggerImpl(className, false);
        loggerImpl.setLevel(rootLevel);
        return loggerImpl;
    }
    
    public static Logger getLogger(int level)
    {
        LoggerImpl loggerImpl = new LoggerImpl((new Throwable()).getStackTrace()[1].getClassName(), true);
        loggerImpl.setLevel(level);
        return loggerImpl;
    }
    
    public static int getRootLevel()
    {
        return rootLevel;
    }
    
    public static void setRootLevel(int rootLevel)
    {
        ConsoleLogFactory.rootLevel = rootLevel;
    }
    
    public static void clearCfg()
    {
        levelMap.clear();
    }
    
}
