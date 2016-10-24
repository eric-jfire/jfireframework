package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用这个注解，意味着可以在dao操作中通过该键来查询得到唯一的返回结果
 * 
 * @author linbin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindBy
{
    
}
