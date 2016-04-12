package com.jfireframework.codejson.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用该注解表明这个方法在被json输出的时候名称会改变
 * 
 * @author 林斌
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonRename
{
    public String value();
}
