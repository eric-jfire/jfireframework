package com.jfireframework.context.bean.build;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Documented
@Inherited
public @interface BuildBy
{
    /**
     * 该类需要由哪一个BeanClassBuilder来进行增强
     * 
     * @return
     */
    public Class<? extends BeanClassBuilder> buildFrom();
    
    /**
     * 该BeanClassBuilder实例启动后的初始化参数
     * 
     * @return
     */
    public String initArgument();
}
