package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;

@Resource
public class ChildPerson extends Person
{
    public void my()
    {
        System.out.println("子类说法");
        hh();
    }
}
