package com.jfireframework.context.test.function.initmethod;

import java.io.File;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.BeanConfig;

public class InitMethodTest
{
    
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        Person person = jfireContext.getBean(Person.class);
        Assert.assertEquals(23, person.getAge());
        Assert.assertEquals("林斌", person.getName());
    }
    
    @Test
    public void testcfg()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        BeanConfig beanConfig = new BeanConfig("p2");
        beanConfig.setPostConstructMethod("initage");
        jfireContext.addBeanConfig(beanConfig);
        Person2 person2 = jfireContext.getBean(Person2.class);
        System.out.println("dsasdasd");
        Assert.assertEquals(12, person2.getAge());
    }
    
    @Test
    public void testfilecfg() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.initmethod");
        jfireContext.readConfig(new File(this.getClass().getClassLoader().getResource("init.json").toURI()));
        Person2 person2 = jfireContext.getBean(Person2.class);
        Assert.assertEquals(12, person2.getAge());
    }
}
