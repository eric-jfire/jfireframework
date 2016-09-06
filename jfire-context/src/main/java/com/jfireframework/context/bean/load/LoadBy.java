package com.jfireframework.context.bean.load;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Documented
@Inherited
public @interface LoadBy
{
    /**
     * 可以提供Bean的工厂bean的名称
     * 
     * @return
     */
    public String factoryBeanName();
}
