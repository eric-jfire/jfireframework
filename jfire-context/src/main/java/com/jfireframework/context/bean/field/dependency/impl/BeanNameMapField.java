package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;

/**
 * Map注入，map的key是bean的名称，也就是value的bean的名称
 * 
 * @author eric(eric@jfire.cn)
 *         
 */
public class BeanNameMapField extends AbstractDependencyField
{
	private Bean[]	dependencyBeans;
	private String	msg;
					
	public BeanNameMapField(Field field, Bean[] beans)
	{
		super(field);
		this.dependencyBeans = beans;
		msg = StringUtil.format("属性{}.{}不能为空", field.getDeclaringClass(), field.getName());
	}
	
	@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
	@Override
	public void inject(Object src, Map<String, Object> beanInstanceMap)
	{
		Map map = (Map) unsafe.getObject(src, offset);
		Verify.notNull(map, msg);
		for (Bean each : dependencyBeans)
		{
			try
			{
				map.put(each.getBeanName(), each.getInstance());
			}
			catch (IllegalArgumentException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
