package com.jfireframework.litl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TempLateConfig
{
    protected String  methodEndFlag;
    protected String  methodStartFlag;
    protected char    _methodStartFlag;
    protected String  varStartFlag;
    protected char    _varStartFlag;
    protected String  varEndFlag;
    protected String  functionStartFlag;
    protected char    _functionStartFlag;
    protected String  functionEndFlag;
    protected boolean devMode;
    
    public TempLateConfig()
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("litl-default.properties");
        Properties properties = new Properties();
        try
        {
            properties.load(inputStream);
            config(properties);
            inputStream.close();
        }
        catch (IOException e)
        {
        }
        inputStream = this.getClass().getResourceAsStream("liti.properties");
        if (inputStream != null)
        {
            try
            {
                properties.load(inputStream);
                config(properties);
                inputStream.close();
            }
            catch (IOException e)
            {
            }
        }
        
    }
    
    private void config(Properties properties)
    {
        String value;
        value = properties.getProperty("methodStartFlag");
        if (value != null)
        {
            methodStartFlag = value;
            _methodStartFlag = value.charAt(0);
        }
        value = properties.getProperty("methodEndFlag");
        if (value != null)
        {
            methodEndFlag = value;
        }
        value = properties.getProperty("functionStartFlag");
        if (value != null)
        {
            functionStartFlag = value;
            _functionStartFlag = value.charAt(0);
        }
        value = properties.getProperty("functionEndFlag");
        if (value != null)
        {
            functionEndFlag = value;
        }
        value = properties.getProperty("varStartFlag");
        if (value != null)
        {
            varStartFlag = value;
            _varStartFlag = value.charAt(0);
        }
        value = properties.getProperty("varEndFlag");
        if (value != null)
        {
            varEndFlag = value;
        }
        value = properties.getProperty("devMode");
        if (value != null)
        {
            devMode = Boolean.parseBoolean(value);
        }
    }
    
    public static void main(String[] args)
    {
        new TempLateConfig();
    }
    
    public String getMethodEndFlag()
    {
        return methodEndFlag;
    }
    
    public String getMethodStartFlag()
    {
        return methodStartFlag;
    }
    
    public char get_methodStartFlag()
    {
        return _methodStartFlag;
    }
    
    public String getVarStartFlag()
    {
        return varStartFlag;
    }
    
    public char get_varStartFlag()
    {
        return _varStartFlag;
    }
    
    public String getVarEndFlag()
    {
        return varEndFlag;
    }
    
    public String getFunctionStartFlag()
    {
        return functionStartFlag;
    }
    
    public char get_functionStartFlag()
    {
        return _functionStartFlag;
    }
    
    public String getFunctionEndFlag()
    {
        return functionEndFlag;
    }
    
    public boolean isDevMode()
    {
        return devMode;
    }
    
}
