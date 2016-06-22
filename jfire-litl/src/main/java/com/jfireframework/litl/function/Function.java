package com.jfireframework.litl.function;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;

public interface Function
{
    /**
     * 框架会自动增加一个参数为当前的行数。也就是params的最后一个元素
     * 
     * @param params
     * @param data
     * @param builder
     * @param template
     */
    public void call(Map<String, Object> data, StringCache cache);
}
