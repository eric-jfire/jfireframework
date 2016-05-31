package com.jfireframework.context.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ThrowEnhance
{
    public String value() default "";
    
    public int order() default 1;
    
    /**
     * 需要进行捕获的异常类型。默认为throwable类型
     * 
     * @return
     */
    public Class<?>[] type() default { Throwable.class };
    
}
