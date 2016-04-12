package com.jfireframework.codejson;

import java.util.HashMap;

public class JsonObject extends HashMap<String, Object> implements Json
{
	private static final long serialVersionUID = 1L;
	
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
		return ((Double) get(key)).floatValue();
	}
	
	public Byte getWByte(String key)
	{
		return ((Long) get(key)).byteValue();
	}
	
	public Short getWShort(String key)
	{
		return ((Long) get(key)).shortValue();
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
}
