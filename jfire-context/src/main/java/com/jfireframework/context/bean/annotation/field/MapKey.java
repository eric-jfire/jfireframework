package com.jfireframework.context.bean.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示map属性字段进行依赖时所需要的信息,可以指定某一个方法的返回值是key
 * 
 * @author 林斌{erci@jfire.cn}
 *         
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface MapKey
{
    public String value();
}
