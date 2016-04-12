package com.jfire.test.rule;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 执行重复次数的测试方法
 * 
 * @author linbin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatTest
{
    /**
     * 重复执行的次数
     * 
     * @return
     */
    public int value();
}
