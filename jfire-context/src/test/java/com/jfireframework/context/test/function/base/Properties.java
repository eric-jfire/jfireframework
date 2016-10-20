package com.jfireframework.context.test.function.base;

import java.io.File;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.test.function.base.data.ImmutablePerson;

public class Properties
{
    @Test
    public void test() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig(new File(this.getClass().getClassLoader().getResource("propertiestest.json").toURI()));
        ImmutablePerson person = jfireContext.getBean(ImmutablePerson.class);
        Assert.assertEquals(12, person.getAge());
    }
}
