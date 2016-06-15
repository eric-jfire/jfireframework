package com.jfireframework.context.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解表示会使用方法的入参作为key的规则参数，方法的返回值值作为缓存值存入
 * 
 * @author linbin
 *
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheGet
{
    /**
     * key的规则
     * 
     * @return
     */
    public String value();
    
    /**
     * 缓存名称
     * 
     * @return
     */
    public String cacheName() default "default";
    
    /**
     * 进行缓存操作的条件
     * 
     * @return
     */
    public String condition() default "";
    
}
