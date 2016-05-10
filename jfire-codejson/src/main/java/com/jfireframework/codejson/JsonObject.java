package com.jfireframework.codejson;

import java.util.HashMap;

public class JsonObject extends HashMap<String, Object> implements Json
{
    private static final long serialVersionUID = 1L;
    private Json              parentNode;
    
    public Object get(String key)
    {
        return super.get(key);
    }
    
    public JsonObject getJsonObject(String key)
    {
        return (JsonObject) get(key);
    }
    
    public JsonArray getJsonArray(String key)
    {
        return (JsonArray) get(key);
    }
    
    public boolean contains(String key)
    {
        return containsKey(key);
    }
    
    public String getWString(String key)
    {
        return (String) get(key);
    }
    
    public Long getWLong(String key)
    {
        return ((Long) get(key));
    }
    
    public Integer getWInteger(String key)
    {
        return ((Long) get(key)).intValue();
    }
    
    public Double getWDouble(String key)
    {
        return ((Double) get(key));
    }
    
    public Boolean getWBoolean(String key)
    {
        return (Boolean) get(key);
    }
    
    public Float getWFloat(String key)
    {
        Double value = ((Double) get(key));
        if (value != null)
        {
            return value.floatValue();
        }
        else
        {
            return null;
        }
    }
    
    public Byte getWByte(String key)
    {
        Long value = ((Long) get(key));
        if (value == null)
        {
            return null;
        }
        return value.byteValue();
    }
    
    public Short getWShort(String key)
    {
        Long value = ((Long) get(key));
        if (value == null)
        {
            return null;
        }
        return value.shortValue();
    }
    
    public Character getWCharacter(String key)
    {
        return ((String) get(key)).charAt(0);
    }
    
    public int getInt(String key)
    {
        return ((Long) get(key)).intValue();
    }
    
    public float getFloat(String key)
    {
        return ((Double) get(key)).floatValue();
    }
    
    public byte getByte(String key)
    {
        return ((Long) get(key)).byteValue();
    }
    
    public char getChar(String key)
    {
        return ((String) get(key)).charAt(0);
    }
    
    public boolean getBoolean(String key)
    {
        return ((Boolean) get(key)).booleanValue();
    }
    
    public double getDouble(String key)
    {
        return ((Double) get(key)).doubleValue();
    }
    
    public long getLong(String key)
    {
        return ((Long) get(key)).longValue();
    }
    
    public short getShort(String key)
    {
        return ((Long) get(key)).shortValue();
    }
    
    @Override
    public void setParentNode(Json json)
    {
        this.parentNode = json;
    }
    
    @Override
    public Json getParentNode()
    {
        return parentNode;
    }
    
    @Override
    public boolean hasParentNode()
    {
        return parentNode != null;
    }
}
