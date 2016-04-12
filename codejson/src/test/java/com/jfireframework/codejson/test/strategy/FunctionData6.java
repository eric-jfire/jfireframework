package com.jfireframework.codejson.test.strategy;

import java.util.HashMap;
import java.util.Map;

public class FunctionData6
{
    private Map<String, String>[] maps = new HashMap[2];
    
    public FunctionData6()
    {
        Map<String, String> m = new HashMap<>();
        m.put("test", "test");
        maps[0] = m;
        m = new HashMap<>();
        m.put("abc", "def");
        maps[1] = m;
    }
    
    public Map<String, String>[] getMaps()
    {
        return maps;
    }
    
    public void setMaps(Map<String, String>[] maps)
    {
        this.maps = maps;
    }
    
}
