package com.jfireframework.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.code.CodeLocation;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.config.BeanAttribute;
import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.config.ContextConfig;

public class JfireContextImpl implements JfireContext
{
	private Map<String, BeanConfig>	configMap	= new HashMap<>();
	private Map<String, Bean>		beanNameMap	= new HashMap<>();
	private Map<Class<?>, Bean>		beanTypeMap	= new HashMap<>();
	private boolean					init		= false;
	private LightSet<String>		classNames	= new LightSet<>();
	private static Logger			logger		= ConsoleLogFactory.getLogger();
	private ClassLoader				classLoader;
									
	public JfireContextImpl()
	{
	}
	
	public JfireContextImpl(String... packageNames)
	{
		addPackageNames(packageNames);
	}
	
	@Override
	public void addPackageNames(String... packageNames)
	{
		Verify.False(init, "不能在容器初始化后再加入需要扫描的包名");
		Verify.notNull(packageNames, "添加扫描的包名有误,不能为null.请检查{}", CodeLocation.getCodeLocation(2));
		LightSet<String> classNames = new LightSet<>();
		for (String each : packageNames)
		{
			if (each == null)
			{
				continue;
			}
			classNames.addAll(PackageScan.scan(each));
		}
		this.classNames.addAll(classNames);
		StringCache cache = new StringCache("共扫描到类：\r\n");
		for (int i = 0; i < classNames.size(); i++)
		{
			cache.append("{}\r\n");
		}
		logger.trace(cache.toString(), (Object[]) classNames.toArray(String.class));
	}
	
	@Override
	public void readConfig(File configFile)
	{
		try
		{
			/** 将配置文件的内容，以json方式读取，并且得到json对象 */
			FileInputStream inputStream = new FileInputStream(configFile);
			byte[] result = new byte[inputStream.available()];
			inputStream.read(result);
			inputStream.close();
			String json = new String(result, Charset.forName("utf-8"));
			ContextConfig config = JsonTool.read(ContextConfig.class, json);
			/** 将配置文件的内容，以json方式读取，并且得到json对象 */
			String[] packageNames = config.getPackageNames();
			if (packageNames != null)
			{
				addPackageNames(packageNames);
			}
			BeanInfo[] beans = config.getBeans();
			if (beans != null)
			{
				for (BeanInfo each : beans)
				{
					String beanName = each.getBeanName();
					String className = each.getClassName();
					boolean prototype = each.isPrototype();
					addBean(beanName, prototype, Class.forName(className));
					Map<String, String> dependencies = each.getDependencies();
					Map<String, String> params = each.getParams();
					if (dependencies.size() > 0 || params.size() > 0 || each.getPostConstructMethod() != null)
					{
						BeanConfig beanConfig = new BeanConfig(beanName);
						beanConfig.getParamMap().putAll(params);
						beanConfig.getDependencyMap().putAll(dependencies);
						beanConfig.setPostConstructMethod(each.getPostConstructMethod());
						addBeanConfig(beanConfig);
					}
				}
			}
			if (config.getBeanConfigs() != null)
			{
				for (BeanAttribute each : config.getBeanConfigs())
				{
					if (each.getParams().size() > 0 || each.getDependencies().size() > 0 || each.getPostConstructMethod() != null)
					{
						BeanConfig beanConfig = new BeanConfig(each.getBeanName());
						beanConfig.getParamMap().putAll(each.getParams());
						beanConfig.getDependencyMap().putAll(each.getDependencies());
						beanConfig.setPostConstructMethod(each.getPostConstructMethod());
						addBeanConfig(beanConfig);
					}
				}
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("配置文件不存在", e);
		}
		catch (IOException e)
		{
			logger.error("解析配置文件出现异常，请检查配置文件是否按照格式要求", e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error("配置的className错误", e);
		}
	}
	
	@Override
	public void addBean(Class<?>... srcs)
	{
		Verify.False(init, "不能在容器初始化后再加入Bean");
		for (Class<?> src : srcs)
		{
			if (src.isAnnotationPresent(Resource.class))
			{
				Bean bean = new Bean(src);
				beanNameMap.put(bean.getBeanName(), bean);
			}
		}
	}
	
	@Override
	public void addBean(String resourceName, boolean prototype, Class<?> src)
	{
		Verify.False(init, "不能在容器初始化后再加入Bean");
		Bean bean = new Bean(resourceName, prototype, src);
		beanNameMap.put(resourceName, bean);
	}
	
	@Override
	public void addBeanConfig(BeanConfig... beanConfigs)
	{
		Verify.False(init, "不能在容器初始化后再加入Bean配置");
		for (BeanConfig beanConfig : beanConfigs)
		{
			configMap.put(beanConfig.getBeanName(), beanConfig);
		}
	}
	
	@Override
	public void initContext()
	{
		addSingletonEntity(JfireContext.class.getName(), this);
		init = true;
		ContextUtil.buildBean(classNames, beanNameMap, classLoader);
		for (Bean each : beanNameMap.values())
		{
			beanTypeMap.put(each.getOriginType(), each);
			if (configMap.containsKey(each.getBeanName()))
			{
				if (configMap.get(each.getBeanName()).getPostConstructMethod() != null)
				{
					each.setPostConstructMethod(ReflectUtil.fastMethod(ReflectUtil.getMethodWithoutParam(configMap.get(each.getBeanName()).getPostConstructMethod(), each.getOriginType())));
				}
			}
		}
		
		/**
		 * 进行aop操作，将aop增强后的class放入对应的bean中。 这步必须在分析bean之前完成。
		 * 因为aop进行增强时会生成子类来替代Bean中的type.
		 * 并且由于aop需要增加若干个类属性(属性上均有Resouce注解用来注入增强类)，所以注入属性数组的生成必须在aop之后
		 */
		AopUtil.enhance(beanNameMap, classLoader);
		ContextUtil.initDependencyAndParamFields(beanNameMap, configMap);
		// 提前实例化单例，避免第一次惩罚以及由于是在单线程中实例化，就不会出现多线程可能的单例被实例化不止一次的情况
		for (Bean bean : beanNameMap.values())
		{
			if (bean.isPrototype() == false)
			{
				bean.getInstance();
			}
		}
		/**
		 * 按照order顺序运行容器初始化结束方法
		 */
		LightSet<ContextInitFinish> tmp = new LightSet<>();
		for (Bean bean : beanNameMap.values())
		{
			if (bean.HasFinishAction())
			{
				tmp.add((ContextInitFinish) bean.getInstance());
			}
		}
		ContextInitFinish[] initFinishs = tmp.toArray(ContextInitFinish.class);
		Arrays.sort(initFinishs, new AescComparator());
		for (ContextInitFinish each : initFinishs)
		{
			logger.trace("准备执行方法{}.afterContextInit", each.getClass().getName());
			try
			{
				each.afterContextInit();
			}
			catch (Exception e)
			{
				logger.error("执行方法{}.afterContextInit发生异常", each.getClass().getName(), e);
			}
		}
	}
	
	@Override
	public Object getBean(String name)
	{
		if (init == false)
		{
			initContext();
		}
		Bean bean = beanNameMap.get(name);
		if (bean != null)
		{
			return bean.getInstance();
		}
		else
		{
			throw new RuntimeException("bean:" + name + "不存在");
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> src)
	{
		if (init == false)
		{
			initContext();
		}
		Bean bean = getBeanInfo(src);
		return (T) bean.getInstance();
	}
	
	@Override
	public Bean getBeanInfo(Class<?> beanClass)
	{
		if (init == false)
		{
			initContext();
		}
		Bean bean = beanTypeMap.get(beanClass);
		if (bean != null)
		{
			return bean;
		}
		throw new RuntimeException("bean" + beanClass.getName() + "不存在");
	}
	
	@Override
	public Bean getBeanInfo(String resName)
	{
		if (init == false)
		{
			initContext();
		}
		return beanNameMap.get(resName);
	}
	
	@Override
	public Bean[] getBeanByAnnotation(Class<? extends Annotation> annotationType)
	{
		if (init == false)
		{
			initContext();
		}
		LightSet<Bean> beans = new LightSet<>();
		for (Bean each : beanNameMap.values())
		{
			if (each.getOriginType().isAnnotationPresent(annotationType))
			{
				beans.add(each);
			}
		}
		return beans.toArray(Bean.class);
	}
	
	@Override
	public void addSingletonEntity(String beanName, Object entity)
	{
		Verify.False(init, "不能在容器初始化后还加入bean,请检查{}", CodeLocation.getCodeLocation(2));
		Bean bean = new Bean(beanName, entity);
		beanNameMap.put(beanName, bean);
	}
	
	@Override
	public Bean[] getBeanByInterface(Class<?> type)
	{
		if (init == false)
		{
			initContext();
		}
		LightSet<Bean> set = new LightSet<>();
		for (Bean each : beanNameMap.values())
		{
			if (type.isAssignableFrom(each.getOriginType()))
			{
				set.add(each);
			}
		}
		return set.toArray(Bean.class);
	}
	
	@Override
	public void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}
	
}
