package com.jfireframework.context.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解表明该方法结束后或者抛出异常时都会自动关闭orm的session
 * 
 * @author 林斌{erci@jfire.cn}
 *         
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SessionAutoClose
{
}
