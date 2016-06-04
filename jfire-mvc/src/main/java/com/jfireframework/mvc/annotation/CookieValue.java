package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CookieValue
{
    /**
     * cookie的名称
     * 
     * @return
     */
    public String value() default "";
    
    /**
     * 如果该cookie不存在，返回一个默认的值
     * 
     * @return
     */
    public String defaultValue() default "";
}
