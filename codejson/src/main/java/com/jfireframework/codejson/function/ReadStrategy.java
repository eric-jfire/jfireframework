package com.jfireframework.codejson.function;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.jfireframework.codejson.JsonTool;

public class ReadStrategy implements Strategy
{
	private Map<Type, JsonReader>		typeStrategy	= new HashMap<>();
	private Map<String, JsonReader>		fieldStrategy	= new HashMap<>();
	private Set<String>					ignoreFields	= new HashSet<>();
	private Map<String, String>			renameFields	= new HashMap<>();
	private JsonReader					reader;
	
	@Override
	public String getRename(String fieldName)
	{
		return renameFields.get(fieldName);
	}
	
	@Override
	public boolean containsRename(String fieldName)
	{
		return renameFields.containsKey(fieldName);
	}
	
	public boolean containsStrategyType(Class<?> type)
	{
		return typeStrategy.containsKey(type);
	}
	
	public JsonReader getReader(Type type)
	{
		reader = typeStrategy.get(type);
		if (reader == null)
		{
			reader = ReaderContext.getReader(type, this);
			typeStrategy.put(type, reader);
			return reader;
		}
		else
		{
			return reader;
		}
	}
	
	public JsonReader getReaderByField(String fieldName)
	{
		return fieldStrategy.get(fieldName);
	}
	
	public void addTypeStrategy(Class<?> ckass, JsonReader jsonReader)
	{
		typeStrategy.put(ckass, jsonReader);
	}
	
	public boolean containsStrategyField(String fieldName)
	{
		return fieldStrategy.containsKey(fieldName);
	}
	
	public void addFieldStrategy(String fieldName, JsonReader reader)
	{
		fieldStrategy.put(fieldName, reader);
		
	}
	
	public void addIgnoreField(String fieldName)
	{
		ignoreFields.add(fieldName);
	}
	
	public boolean ignore(String fieldName)
	{
		return ignoreFields.contains(fieldName);
	}
	
	public void addRenameField(String originName, String rename)
	{
		renameFields.put(originName, rename);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Type entityClass, String str)
	{
		return (T) getReader(entityClass).read(entityClass, JsonTool.fromString(str));
	}
	
}
