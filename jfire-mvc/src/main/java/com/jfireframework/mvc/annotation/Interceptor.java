package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在方法上，表明需要被对应的拦截器拦截判断。注解的值和拦截的值相同时就表明需要被拦截了。
 * 
 * @author linbin
 *
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor
{
    public String value();
}
