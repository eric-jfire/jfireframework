package com.jfire.test.rule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MutiThreadTest
{
    public int threadNums();
    
    public int repeatTimes();
}
