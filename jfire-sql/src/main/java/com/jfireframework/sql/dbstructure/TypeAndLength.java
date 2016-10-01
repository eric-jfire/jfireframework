package com.jfireframework.sql.dbstructure;

public class TypeAndLength
{
    private String type;
    private int    length;
    
    public TypeAndLength(String type, int length)
    {
        this.type = type;
        this.length = length;
    }
    
    public String getDbType()
    {
        return type + '(' + length + ')';
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public void setLength(int length)
    {
        this.length = length;
    }
    
}
