package com.jfireframework.context.test.function.beanfactory;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class FactoryTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.beanfactory");
        Person person = (Person) jfireContext.getBean("person");
        Assert.assertEquals("aaaa", person.getName());
    }
}
