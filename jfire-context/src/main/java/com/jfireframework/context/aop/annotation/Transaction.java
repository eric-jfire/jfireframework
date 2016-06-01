package com.jfireframework.context.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解表明该类的公共方法或者注解方法是一个事务方法
 * 
 * @author linbin
 * 
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Transaction
{
    /**
     * 识别到该异常进行回滚。默认为Exception
     * 
     * @return
     */
    public Class<?>[] exceptions() default { Throwable.class };
}
