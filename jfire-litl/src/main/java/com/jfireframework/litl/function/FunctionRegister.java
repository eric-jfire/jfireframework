package com.jfireframework.litl.function;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.function.impl.Include;

public class FunctionRegister
{
    private static Map<String, Function> funcs = new HashMap<String, Function>();
    
    static
    {
        funcs.put("include", new Include());
    }
    
    public static Function get(String name)
    {
        Function function = funcs.get(name);
        if (function == null)
        {
            throw new UnSupportException("方法" + name + "不存在，请检查");
        }
        else
        {
            return function;
        }
    }
}
