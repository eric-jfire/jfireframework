package com.jfireframework.codejson.test.strategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

public class FunctionData9
{
    private Object data;
    
    public FunctionData9()
    {
        Map<String, Date> map = new HashMap<String, Date>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            map.put("sda", format.parse("2015-11-16"));
            data = map;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
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
