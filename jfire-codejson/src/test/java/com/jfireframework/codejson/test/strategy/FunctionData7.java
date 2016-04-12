package com.jfireframework.codejson.test.strategy;

import java.util.HashMap;
import java.util.Map;

public class FunctionData7
{
    private Object data;
    
    public FunctionData7()
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "121212");
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
