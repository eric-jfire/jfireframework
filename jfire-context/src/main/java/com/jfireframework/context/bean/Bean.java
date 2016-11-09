package com.jfireframework.context.bean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import com.jfireframework.context.aop.EnhanceAnnoInfo;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.param.ParamField;
import com.jfireframework.context.config.BeanInfo;
import sun.reflect.MethodAccessor;

public interface Bean
{
    
    public Object getInstance();
    
    /**
     * field进行属性注入的时候使用这个方法，这样如果需要循环引用，则因为大家都在一个map中，可以避免循环引用无限循环
     * 
     * @param beanInstanceMap
     * @return
     */
    public Object getInstance(Map<String, Object> beanInstanceMap);
    
    public String getBeanName();
    
    public Class<?> getType();
    
    public boolean isPrototype();
    
    public void setInjectFields(DependencyField[] injectFields);
    
    public boolean HasFinishAction();
    
    public void addEnhanceBean(Bean bean);
    
    public void setType(Class<?> type);
    
    public Class<?> getOriginType();
    
    public void setParamFields(ParamField[] paramFields);
    
    public void addTxMethod(Method method);
    
    public void addResMethod(Method method);
    
    public List<Method> getTxMethodSet();
    
    public List<Method> getResMethods();
    
    public boolean canEnhance();
    
    public boolean canInject();
    
    public List<EnhanceAnnoInfo> getEnHanceAnnos();
    
    public boolean needEnhance();
    
    public void setPostConstructMethod(MethodAccessor postConstructMethod);
    
    public void addCacheMethod(Method method);
    
    public List<Method> getCacheMethods();
    
    public BeanInfo getBeanInfo();
    
    public void setBeanConfig(BeanInfo beanInfo);
    
}
