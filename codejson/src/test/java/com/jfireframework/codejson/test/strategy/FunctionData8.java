package com.jfireframework.codejson.test.strategy;

import java.util.HashMap;
import java.util.Map;

public class FunctionData8
{
    private Object data;
    
    public FunctionData8()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("你好", "林斌");
        data = map;
    }
    
    public Object getData()
    {
        return data;
    }
    
    public void setData(Object data)
    {
        this.data = data;
    }
    
}
