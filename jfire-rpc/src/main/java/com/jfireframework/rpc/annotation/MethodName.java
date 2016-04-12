package com.jfireframework.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface MethodName
{
    /**
     * 代表远程调用时的方法名称
     * 
     * @return
     */
    public String value();
}
