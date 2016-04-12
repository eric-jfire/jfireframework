package com.jfireframework.context.aop.annotation;

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
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
public @interface Transaction
{
    /**
     * 识别到该异常进行回滚。默认为Exception
     * 
     * @return
     */
    public Class<?>[]exceptions() default { Throwable.class };
}
