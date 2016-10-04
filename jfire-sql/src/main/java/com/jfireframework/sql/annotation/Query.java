package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询sql的注解。使用该注解表明会发出查询语句。 根据返回结果类型数组的长度和类型有不同的含义。 如果方法的返回类型是List，则依照以下方式判断。
 * 如果方法的返回类型是对象并且是基本类型.则返回的数据必须是单行单列.
 * 如果方法的返回类型是对象并且不是基本类型,则返回的数据是单行,并且将该行数据转换成为对象实例
 * 如果返回的类型是List<T>的形式,则根据T的类型做一进步判断.是基本类型,则数据应该是多行单列,取出即可.如果是对象类型,则按照对象实例进行转换
 * 如果返回类型是List<Object[]>,则使用注解中returnTypes的值对结果进行获取.并且这些类型都必须是基本类型
 * 
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query
{
    /**
     * 查询语句
     * 
     * @return
     */
    public String sql();
    
    /**
     * 方法的形参名称
     * 
     * @return
     */
    public String paramNames();
    
    /**
     * 方法查询返回的类型
     * 
     * @return
     */
    public Class<?>[] returnTypes() default {};
    
    /**
     * 默认的查询总数的sql语句。为空则让系统自动生成
     * 
     * @return
     */
    public String countSql() default "";
    
    public String selectFields() default "";
}
