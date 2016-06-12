package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Resource;
import com.jfireframework.context.util.AliasFor;

/**
 * 代表注解类是一个action类
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Resource(shareable = true)
public @interface Controller
{
    /**
     * 包的整体请求路径前缀，默认不填写的话为类的简单名称
     * 
     * @return
     */
    @AliasFor(annotation = Resource.class, value = "name")
    public String value() default "";
}
