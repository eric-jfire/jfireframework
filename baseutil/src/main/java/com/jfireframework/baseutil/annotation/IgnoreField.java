package com.jfireframework.baseutil.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解表明这个属性在分析操作中被忽略
 * 
 * @author windfire(windfire@zailanghua.com)
 *         
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreField
{

}
