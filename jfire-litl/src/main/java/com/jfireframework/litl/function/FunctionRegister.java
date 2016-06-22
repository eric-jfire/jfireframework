package com.jfireframework.litl.function;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.function.impl.Include;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class FunctionRegister
{
    private static Map<String, Constructor<? extends Function>> funcs = new HashMap<String, Constructor<? extends Function>>();
    
    static
    {
        try
        {
            funcs.put("include", Include.class.getConstructor(Object[].class, LineInfo.class, Template.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static Function get(String name, Object[] params, LineInfo info, Template template)
    {
        Constructor<? extends Function> constructor = funcs.get(name);
        if (constructor == null)
        {
            throw new UnSupportException("方法" + name + "不存在，请检查");
        }
        else
        {
            try
            {
                return constructor.newInstance(params, info, template);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
            
        }
    }
}
