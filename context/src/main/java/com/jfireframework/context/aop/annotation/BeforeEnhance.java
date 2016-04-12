package com.jfireframework.context.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 代表该方法是一个aop增强描述方法,代表被注解方法是一个前置增强
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeEnhance
{
    public String value() default "";
    
    public int order() default 1;
}
