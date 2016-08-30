package com.jfireframework.context.test.function.loader;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class HolderTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.loader");
        Person person = jfireContext.getBean(Person.class);
        Assert.assertEquals("name", person.getName());
        Home home = jfireContext.getBean(Home.class);
        Assert.assertEquals(100, home.getLength());
    }
}
