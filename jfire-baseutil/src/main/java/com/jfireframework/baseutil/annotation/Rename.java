package com.jfireframework.baseutil.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解表明为当前的属性名称另外起一个名字
 * 
 * @author windfire(windfire@zailanghua.com)
 *         
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Rename
{
    public String value();
}
