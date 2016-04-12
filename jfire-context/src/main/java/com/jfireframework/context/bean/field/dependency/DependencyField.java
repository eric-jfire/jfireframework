package com.jfireframework.context.bean.field.dependency;

import java.util.Map;

public interface DependencyField
{
    /**
     * 将所依赖的bean的实例注入
     * 
     * @param src 被注入的对象
     * @param beanInstanceMap bean实例map。key为bean的名称
     */
    public void inject(Object src, Map<String, Object> beanInstanceMap);
}
