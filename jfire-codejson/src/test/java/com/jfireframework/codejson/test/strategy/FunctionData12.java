package com.jfireframework.codejson.test.strategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FunctionData12
{
    private ArrayList<Date> list = new ArrayList<>();
    
    public FunctionData12()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            list.add(format.parse("2015-11-16"));
            list.add(format.parse("2015-11-11"));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public ArrayList<Date> getList()
    {
        return list;
    }
    
    public void setList(ArrayList<Date> list)
    {
        this.list = list;
    }
    
}
