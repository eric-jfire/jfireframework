package com.jfireframework.context.bean.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.context.bean.BeanBuilder;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Documented
@Inherited
public @interface BuildBy
{
    public Class<? extends BeanBuilder> value();
}
