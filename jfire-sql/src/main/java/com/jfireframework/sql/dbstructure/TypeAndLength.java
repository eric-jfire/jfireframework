package com.jfireframework.sql.dbstructure;

public class TypeAndLength
{
    private final String type;
    private final int    length;
    
    public TypeAndLength(String type, int length)
    {
        this.type = type;
        this.length = length;
    }
    
    public String getDbType()
    {
        if (length != 0)
        {
            return type + '(' + length + ')';
        }
        else
        {
            return type;
        }
    }
    
    public String getType()
    {
        return type;
    }
    
    public int getLength()
    {
        return length;
    }
}
