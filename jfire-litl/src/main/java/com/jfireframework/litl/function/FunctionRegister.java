package com.jfireframework.litl.function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class FunctionRegister
{
    private static Map<String, Class<? extends Function>> funcs = new ConcurrentHashMap<String, Class<? extends Function>>();
    
    public static void register(String functionName, Class<? extends Function> type)
    {
        funcs.put(functionName, type);
    }
    
    public static Function get(String name, Object[] params, LineInfo info, Template template)
    {
        Class<? extends Function> constructor = funcs.get(name);
        if (constructor == null)
        {
            throw new UnSupportException("方法" + name + "不存在，请检查");
        }
        else
        {
            try
            {
                Function function = constructor.newInstance();
                function.init(params, info, template);
                return function;
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
            
        }
    }
}
