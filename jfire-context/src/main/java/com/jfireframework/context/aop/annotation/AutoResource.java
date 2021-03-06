package com.jfireframework.context.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在方法上,代码会自动打开和关闭方法中会需要使用到的资源
 * 
 * @author 林斌{erci@jfire.cn}
 * 
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AutoResource
{
    /**
     * 识别到该异常进行自动关闭,其余异常不操作
     * 
     * @return
     */
    public Class<?>[] exceptions() default { Throwable.class };
}
