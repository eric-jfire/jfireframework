package com.jfireframework.context.bean.impl;

import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.param.ParamField;
import com.jfireframework.context.bean.load.BeanLoadFactory;

public class LoadByBean extends AbstractBean
{
    
    private final JfireContext jfireContext;
    private final String       factoryBeanName;
    
    public LoadByBean(JfireContext jfireContext, Class<?> ckass, String factoryBeanName)
    {
        this.jfireContext = jfireContext;
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
    public Object getInstance()
    {
        return getInstance(beanInstanceMap.get());
    }
    
    @Override
    public Object getInstance(Map<String, Object> beanInstanceMap)
    {
        return get(beanInstanceMap);
    }
    
    private Object get(Map<String, Object> beanInstanceMap)
    {
        if (singletonInstance == null)
        {
            BeanLoadFactory factory = (BeanLoadFactory) jfireContext.getBean(factoryBeanName);
            singletonInstance = factory.load(originType);
            for (DependencyField each : injectFields)
            {
                each.inject(singletonInstance, beanInstanceMap);
            }
            for (ParamField each : paramFields)
            {
                each.setParam(singletonInstance);
            }
        }
        return singletonInstance;
    }
}
