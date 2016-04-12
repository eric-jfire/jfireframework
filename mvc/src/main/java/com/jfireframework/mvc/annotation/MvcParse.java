package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解表明将使用对应的格式转换来完成字符串到属性的转换
 * 
 * @author 林斌
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MvcParse
{
    /**
     * 日期的转换格式
     * 
     * @return
     */
    public String date_format() default "yyyy-MM-dd HH:mm:ss";
}
