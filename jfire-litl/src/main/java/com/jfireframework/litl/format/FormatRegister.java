package com.jfireframework.litl.format;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.format.impl.DateFormat;

public class FormatRegister
{
    private static Map<Class<?>, Format> formats = new HashMap<Class<?>, Format>();
    
    static
    {
        formats.put(Date.class, new DateFormat());
    }
    
    public static Format get(Class<?> type)
    {
        Format format = formats.get(type);
        if (format == null)
        {
            throw new UnSupportException("格式化类型:" + type.getName() + "不存在，请检查");
        }
        else
        {
            return format;
        }
    }
}
