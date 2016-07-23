package com.jfireframework.context.test.function.aliastest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.context.aliasanno.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@InitMethod(name = "")
public @interface MyMethod
{
    @AliasFor(annotation = InitMethod.class, value = "name")
    public String load();
}
