package com.jfireframework.context.config;

import java.util.HashMap;

public class BeanAttribute
{
	private String					beanName;
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
