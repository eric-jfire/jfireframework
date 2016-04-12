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
        if (length == 0)
        {
            return type;
        }
        else
        {
            return type + "(" + length + ")";
        }
    }
    
    public String getDbType(int length)
    {
        if (length == 0)
        {
            return type;
        }
        else
        {
            return type + "(" + length + ")";
        }
    }
}
