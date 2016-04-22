package com.jfireframework.codejson.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用该注解表明这个方法会被忽略
 * 
 * @author 林斌
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonIgnore
{
    /**
     * 代表是否忽略策略配置，强制执行
     * 
     * @return
     */
    public boolean force() default false;
}
