package com.jfireframework.context.test.function.aliastest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Resource;
import com.jfireframework.context.aliasanno.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Resource(shareable = true)
public @interface SingleTon
{
    @AliasFor(annotation = Resource.class, value = "name")
    public String value() default "";
}
