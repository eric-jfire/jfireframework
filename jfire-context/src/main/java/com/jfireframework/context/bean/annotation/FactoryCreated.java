package com.jfireframework.context.bean.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.context.bean.BeanFactory;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Documented
@Inherited
public @interface FactoryCreated
{
    /**
     * 这个bean由哪一个BeanFactory来创建
     * 
     * @return
     */
    public Class<BeanFactory> value();
}
