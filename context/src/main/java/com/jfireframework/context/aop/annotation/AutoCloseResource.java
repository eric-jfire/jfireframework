package com.jfireframework.context.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在方法上,代表会自动关闭方法中打开的资源
 * 
 * @author 林斌{erci@jfire.cn}
 *         
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AutoCloseResource
{
    /**
     * 识别到该异常进行自动关闭,其余异常不操作
     * 
     * @return
     */
    public Class<?>[]exceptions() default { Throwable.class };
}
