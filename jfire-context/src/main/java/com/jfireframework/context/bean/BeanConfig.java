package com.jfireframework.context.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * bean的配置类，用来初始化bean的一些属性信息
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class BeanConfig
{
	/** 该Bean配置所对应的bean的名称 */
	private String					beanName;
	/** 属性和对应的参数值的map */
	private HashMap<String, String>	paramsMap		= new HashMap<>();
	/** 对象属性和所需要依赖的bean名称 */
	private Map<String, String>		dependencyMap	= new HashMap<>();
	// bean初始化完毕，依赖关系注入，参数注入后，调用的初始化方法
	private String					postConstructMethod;
									
	/**
	 * 指定该配置对应的bean的名称
	 * 
	 * @param beanName
	 */
	public BeanConfig(String beanName)
	{
		this.beanName = beanName;
	}
	
	/**
	 * 放入属性的名称和值，值以string的形式表现，具体到每个属性容器负责转换
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public BeanConfig putParam(String name, String value)
	{
		paramsMap.put(name, value);
		return this;
	}
	
	public String getBeanName()
	{
		return beanName;
	}
	
	public Map<String, String> getParamMap()
	{
		return paramsMap;
	}
	
	/**
	 * 放入需要注入的field名称和注入的bean名称。其中bean名称可以以;隔开，来适应field是list或者set类型
	 * 
	 * @param fieldName 需要注入的Field名称
	 * @param denpendencyStr 需要注入的依赖字符串,规则遵循配置文件规则
	 */
	public void putDependencyStr(String fieldName, String denpendencyStr)
	{
		dependencyMap.put(fieldName, denpendencyStr);
	}
	
	public Map<String, String> getDependencyMap()
	{
		return dependencyMap;
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
