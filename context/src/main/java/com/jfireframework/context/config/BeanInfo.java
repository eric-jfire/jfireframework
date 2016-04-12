package com.jfireframework.context.config;

import java.util.HashMap;

public class BeanInfo
{
	private String					beanName;
	private String					className;
	private boolean					prototype;
	private HashMap<String, String>	params			= new HashMap<String, String>();
	private HashMap<String, String>	dependencies	= new HashMap<>();
	private String					postConstructMethod;
									
	public String getBeanName()
	{
		return beanName;
	}
	
	public void setBeanName(String beanName)
	{
		this.beanName = beanName;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public boolean isPrototype()
	{
		return prototype;
	}
	
	public void setPrototype(boolean prototype)
	{
		this.prototype = prototype;
	}
	
	public HashMap<String, String> getParams()
	{
		return params;
	}
	
	public void setParams(HashMap<String, String> params)
	{
		this.params = params;
	}
	
	public HashMap<String, String> getDependencies()
	{
		return dependencies;
	}
	
	public void setDependencies(HashMap<String, String> dependencies)
	{
		this.dependencies = dependencies;
	}
	
	public String getPostConstructMethod()
	{
		return postConstructMethod;
	}
	
	public void setPostConstructMethod(String postConstructMethod)
	{
		this.postConstructMethod = postConstructMethod;
	}
	
}
