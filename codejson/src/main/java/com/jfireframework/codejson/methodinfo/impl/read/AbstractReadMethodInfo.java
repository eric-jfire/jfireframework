package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.methodinfo.ReadMethodInfo;
import com.jfireframework.codejson.util.NameTool;

public class AbstractReadMethodInfo implements ReadMethodInfo
{
	protected String				str;
	protected Method				method;
	protected String				entityName;
	protected String				strategyFieldName;
	protected String				fieldName;
	protected static Set<Class<?>>	wrapperSet	= new HashSet<>();
	protected ReadStrategy			strategy;
	static
	{
		wrapperSet.add(String.class);
		wrapperSet.add(Boolean.class);
		wrapperSet.add(Integer.class);
		wrapperSet.add(Long.class);
		wrapperSet.add(Short.class);
		wrapperSet.add(Float.class);
		wrapperSet.add(Double.class);
		wrapperSet.add(Short.class);
		wrapperSet.add(Byte.class);
		wrapperSet.add(Character.class);
		wrapperSet.equals(String.class);
	}
	
	public AbstractReadMethodInfo(Method method, ReadStrategy strategy)
	{
		this.method = method;
		fieldName = NameTool.getNameFromMethod(method, strategy);
		this.strategy = strategy;
		strategyFieldName = method.getDeclaringClass().getName() + '.' + fieldName;
		entityName = "((" + method.getDeclaringClass().getName() + ")entity).";
	}
	
	public String getOutput()
	{
		return str;
	}
	
	protected Class<?> getParamType()
	{
		return method.getParameterTypes()[0];
	}
	
}
