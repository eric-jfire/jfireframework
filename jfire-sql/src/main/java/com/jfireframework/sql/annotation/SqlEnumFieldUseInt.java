package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用这个注解意味着当使用enum作为属性类型的时候，在进行数据库操作时使用其int值
 * 注意，该注解使用在Enum类型上
 * 
 * @author linbin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlEnumFieldUseInt
{
    
}
