package com.jfireframework.dbunit.table;


public class Row
{
    public String[]           data;
    
    public Row(String[] data)
    {
        this.data = data;
    }
    
    public String[] getData()
    {
        return data;
    }
    
    public void setData(String[] data)
    {
        this.data = data;
    }
    
}
