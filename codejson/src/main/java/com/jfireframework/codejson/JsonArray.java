package com.jfireframework.codejson;

import java.util.ArrayList;

public class JsonArray extends ArrayList<Object> implements Json
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public JsonObject getJsonObject(int index)
    {
        return (JsonObject) get(index);
    }
    
    public JsonArray getJsonArray(int index)
    {
        return (JsonArray) get(index);
    }
    
    public String getWString(int index)
    {
        return (String) get(index);
    }
    
    public Long getWLong(int index)
    {
        return ((Long) get(index));
    }
    
    public Double getWDouble(int index)
    {
        return ((Double) get(index));
    }
    
    public Integer getWInteger(int index)
    {
        return ((Long) get(index)).intValue();
    }
    
    public Short getWShort(int index)
    {
        return ((Long) get(index)).shortValue();
    }
    
    public Float getWFloat(int index)
    {
        return ((Double) get(index)).floatValue();
    }
    
    public Character getWCharacter(int index)
    {
        return ((String) get(index)).charAt(0);
    }
    
    public Boolean getWBoolean(int index)
    {
        return (Boolean) get(index);
    }
    
    public Byte getWByte(int index)
    {
        return ((Long) get(index)).byteValue();
    }
    
    public Boolean getBoolean(int index)
    {
        return (Boolean) get(index);
    }
    
    public int getInt(int index)
    {
        return ((Long) get(index)).intValue();
    }
    
    public float getFloat(int index)
    {
        return ((Double) get(index)).floatValue();
    }
    
    public long getLong(int index)
    {
        return ((Long) get(index)).longValue();
    }
    
    public short getShort(int index)
    {
        return ((Long) get(index)).shortValue();
    }
    
    public char getChar(int index)
    {
        return ((String) get(index)).charAt(0);
    }
    
    public byte getByte(int index)
    {
        return ((Long) get(index)).byteValue();
    }
    
    public double getDouble(int index)
    {
        return ((Double) get(index)).doubleValue();
    }
    
}
