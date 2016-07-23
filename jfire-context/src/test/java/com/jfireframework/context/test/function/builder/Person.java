package com.jfireframework.context.test.function.builder;

import javax.annotation.Resource;
import com.jfireframework.context.bean.annotation.BuildBy;

@Resource
@BuildBy(TestBuilder.class)
public interface Person
{
    public String getName();
}
