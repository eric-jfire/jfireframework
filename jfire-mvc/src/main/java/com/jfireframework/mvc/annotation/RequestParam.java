package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法从http请求中获取的参数
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestParam
{
    /** 该属性的名称 */
    public String value();
    
    /**
     * 如果这个属性是date类型，默认的转换格式
     * @return
     */
    public String dateFormat() default "";
}
