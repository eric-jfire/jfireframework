package com.jfireframework.codejson.test.strategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FunctionData
{
    private Map<String, String> map  = new HashMap<String, String>();
    private Map<String, String> map2 = new HashMap<>();
    private Map<Integer, Date>  map3 = new HashMap<>();
    
    public FunctionData()
    {
        map.put("test", "test");
        map2.put("test", "test");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            map3.put(1, dateFormat.parse("2015-11-15"));
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Map<Integer, Date> getMap3()
    {
        return map3;
    }
    
    public void setMap3(Map<Integer, Date> map3)
    {
        this.map3 = map3;
    }
    
    public Map<String, String> getMap2()
    {
        return map2;
    }
    
    public void setMap2(Map<String, String> map2)
    {
        this.map2 = map2;
    }
    
    public Map<String, String> getMap()
    {
        return map;
    }
    
    public void setMap(Map<String, String> map)
    {
        this.map = map;
    }
    
}
