package com.jfireframework.context.bean.impl;

import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.load.BeanLoadFactory;

public class LoadByBean extends AbstractBean
{
    private final String factoryBeanName;
    private Bean         factoryBean;
    
    public LoadByBean(Class<?> ckass, String factoryBeanName)
    {
        this.factoryBeanName = factoryBeanName;
        prototype = false;
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, ckass);
        if ("".equals(resource.name()))
        {
            beanName = ckass.getName();
        }
        else
        {
            beanName = resource.name();
        }
        type = ckass;
        originType = ckass;
        canEnhance = false;
        canInject = false;
    }
    
    @Override
    public void decorateSelf(Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap)
    {
        factoryBean = beanNameMap.get(factoryBeanName);
    }
    
    @Override
    public Object getInstance()
    {
        BeanLoadFactory factory = (BeanLoadFactory) factoryBean.getInstance();
        return factory.load(originType);
    }
    
    @Override
    public Object getInstance(Map<String, Object> beanInstanceMap)
    {
        if (beanInstanceMap.containsKey(beanName))
        {
            return beanInstanceMap.get(beanName);
        }
        else
        {
            BeanLoadFactory factory = (BeanLoadFactory) factoryBean.getInstance(beanInstanceMap);
            Object entity = factory.load(originType);
            beanInstanceMap.put(beanName, entity);
            return entity;
        }
    }
    
}
