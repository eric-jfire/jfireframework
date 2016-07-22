package com.jfireframework.context.test.function.beanfactory;

import javax.annotation.Resource;
import com.jfireframework.context.bean.annotation.FactoryCreated;

@Resource(name = "person")
@FactoryCreated(TestFactory.class)
public interface Person
{
    public String getName();
}
