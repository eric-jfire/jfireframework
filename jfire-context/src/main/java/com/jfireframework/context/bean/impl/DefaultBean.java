package com.jfireframework.context.bean.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.param.ParamField;

public class DefaultBean extends AbstractBean
{
    public DefaultBean(Class<?> ckass)
    {
        if (AnnotationUtil.isPresent(Resource.class, ckass) == false)
        {
            throw new NullPointerException();
        }
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, ckass);
        if ("".equals(resource.name()))
        {
            beanName = ckass.getName();
        }
        else
        {
            beanName = resource.name();
        }
        prototype = (resource.shareable() == false);
        configBean(beanName, prototype, ckass);
    }
    
    public DefaultBean(String beanName, Class<?> ckass, boolean prototype)
    {
        configBean(beanName, prototype, ckass);
    }
    
    /**
     * 配置Bean信息，设置该bean的资源名称，是否多例，以及该Bean的类型和原始类型。并且判断是否实现了容器初始化完毕接口
     * 
     * @param beanName 该bean的名称
     * @param prototype 该bean是否多例
     * @param src 该bean的类
     */
    protected void configBean(String beanName, boolean prototype, Class<?> src)
    {
        this.beanName = beanName;
        type = src;
        originType = src;
        this.prototype = prototype;
        if (ContextInitFinish.class.isAssignableFrom(src))
        {
            hasFinishAction = true;
        }
        for (Method each : ReflectUtil.getAllMehtods(src))
        {
            if (each.isAnnotationPresent(PostConstruct.class))
            {
                Verify.True(postConstructMethod == null, "一个类只能有一个方法使用注解PostConstruct");
                Verify.True(each.getParameterTypes().length == 0, "使用PostConstruct注解的方法必须是无参方法，请检查{}.{}", each.getDeclaringClass().getName(), each.getName());
                postConstructMethod = ReflectUtil.fastMethod(each);
            }
        }
    }
    
    @Override
    public Object getInstance()
    {
        HashMap<String, Object> map = beanInstanceMap.get();
        map.clear();
        return getInstance(map);
        
    }
    
    @Override
    public Object getInstance(Map<String, Object> beanInstanceMap)
    {
        if (beanInstanceMap.containsKey(beanName))
        {
            return beanInstanceMap.get(beanName);
        }
        if (prototype)
        {
            return buildInstance(beanInstanceMap);
        }
        else
        {
            if (singletonInstance == null)
            {
                return buildInstance(beanInstanceMap);
            }
            else
            {
                return singletonInstance;
            }
        }
    }
    
    private Object buildInstance(Map<String, Object> beanInstanceMap)
    {
        try
        {
            Object instance = type.newInstance();
            beanInstanceMap.put(beanName, instance);
            for (DependencyField each : injectFields)
            {
                each.inject(instance, beanInstanceMap);
            }
            for (ParamField each : paramFields)
            {
                each.setParam(instance);
            }
            if (postConstructMethod != null)
            {
                postConstructMethod.invoke(instance, null);
            }
            if (prototype == false)
            {
                singletonInstance = instance;
            }
            return instance;
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("初始化bean实例错误，实例名称:{},对象类名:{}", beanName, type.getName()), e);
        }
    }
}
