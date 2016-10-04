package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明该字段是一个数据库的映射列
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column
{
    /**
     * 映射的数据库的列名
     * 
     * @return
     */
    public String name() default "";
    
    /**
     * 表示这个属性在保存的时候会被忽略.
     * 也就是说在生成的dao的curd操作中，读取操作时该属性会生效，也会从数据库拿到对应的值。但是更新操作和插入操作则不会有该属性的参与。
     * 
     * @return
     */
    public boolean saveIgnore() default false;
    
    /**
     * 表示这个属性在dao操作中会被忽略。也就是说在生成的dao的curd语句中，该属性是不存在的
     * 
     * @return
     */
    public boolean daoIgnore() default false;
    
    public int length() default -1;
}
