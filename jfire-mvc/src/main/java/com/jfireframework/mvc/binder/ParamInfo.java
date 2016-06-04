package com.jfireframework.mvc.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ParamInfo
{
    private String       prefix;
    private Type         entityClass;
    private Object       defaultValue;
    private Annotation[] annotations;
    
    public Annotation[] getAnnotations()
    {
        return annotations;
    }
    
    public void setAnnotations(Annotation[] annotations)
    {
        this.annotations = annotations;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
    public Type getEntityClass()
    {
        return entityClass;
    }
    
    public void setEntityClass(Type entityClass)
    {
        this.entityClass = entityClass;
    }
    
    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }
    
}
