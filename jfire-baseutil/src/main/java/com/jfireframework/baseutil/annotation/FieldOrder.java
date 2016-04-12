package com.jfireframework.baseutil.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * 属性排序注解，通过注解为属性设置一个顺序
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FieldOrder
{
    public int value();
}
