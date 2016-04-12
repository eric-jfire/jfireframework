package com.jfireframework.context.test.function.base.maptest;

import java.util.HashMap;
import java.util.Map;

public class House
{
    private Map<String, Person> map = new HashMap<String, Person>();
    
    public Map<String, Person> getMap()
    {
        return map;
    }
    
    public void setMap(Map<String, Person> map)
    {
        this.map = map;
    }
}
