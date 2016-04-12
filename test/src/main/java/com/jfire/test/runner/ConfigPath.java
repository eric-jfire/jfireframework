package com.jfire.test.runner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 指定配置文件的注解
 * 
 * @author linbin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPath
{
    public String value();
}
