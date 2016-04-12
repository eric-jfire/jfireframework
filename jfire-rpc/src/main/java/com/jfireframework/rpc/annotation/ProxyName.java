package com.jfireframework.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface ProxyName
{
    /**
     * 代表远端调用的时候的代理名称
     * 
     * @return
     */
    public String value();
}
