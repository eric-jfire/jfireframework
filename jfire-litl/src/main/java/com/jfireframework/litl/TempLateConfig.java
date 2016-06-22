package com.jfireframework.litl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.litl.format.Format;
import com.jfireframework.litl.format.NameFormatRegister;
import com.jfireframework.litl.format.TypeFormatRegister;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.function.FunctionRegister;

public class TempLateConfig
{
    protected String  methodEndFlag;
    protected String  methodStartFlag;
    protected char    _methodStartFlag;
    protected String  varStartFlag;
    protected char    _varStartFlag;
    protected String  varEndFlag;
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
    
    @SuppressWarnings("unchecked")
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
        Set<String> names = properties.stringPropertyNames();
        for (String each : names)
        {
            if (each.startsWith("FT."))
            {
                String formatClassName = properties.getProperty(each);
                try
                {
                    String formatName = each.substring(3);
                    NameFormatRegister.register(formatName, (Class<? extends Format>) Class.forName(formatClassName));
                }
                catch (ClassNotFoundException e)
                {
                    throw new JustThrowException(e);
                }
            }
            else if (each.startsWith("FTC."))
            {
                String formatClassName = properties.getProperty(each);
                try
                {
                    String formatType = each.substring(4);
                    TypeFormatRegister.register(Class.forName(formatType), (Class<? extends Format>) Class.forName(formatClassName));
                }
                catch (ClassNotFoundException e)
                {
                    throw new JustThrowException(e);
                }
            }
            else if (each.startsWith("FN."))
            {
                String functionName = each.substring(3);
                String functionClassName = properties.getProperty(each);
                try
                {
                    FunctionRegister.register(functionName, (Class<? extends Function>) Class.forName(functionClassName));
                }
                catch (ClassNotFoundException e)
                {
                    throw new JustThrowException(e);
                }
            }
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
    
    public boolean isDevMode()
    {
        return devMode;
    }
    
}
