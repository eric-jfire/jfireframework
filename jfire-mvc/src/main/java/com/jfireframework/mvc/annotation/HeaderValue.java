package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HeaderValue
{
    /**
     * header的名称，如果不填写默认为参数名字
     * 
     * @return
     */
    public String value() default "";
    
    /*
     * 如果该header不存在的时候给予一个默认值
     */
    public String defaultValue() default "";
}
