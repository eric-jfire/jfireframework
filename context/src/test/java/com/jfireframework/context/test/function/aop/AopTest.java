package com.jfireframework.context.test.function.aop;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.Bean;

public class AopTest
{
    @Test
    public void testAnnoExist() throws NoSuchMethodException, SecurityException
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Bean bean = jfireContext.getBeanInfo(Person.class);
        Method method = bean.getType().getDeclaredMethod("sayHello");
        assertEquals("注解保留", method.getAnnotation(Resource.class).name());
    }
    
    @Test
    public void beforetest()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        assertEquals("前置拦截", person.sayHello("你好"));
    }
    
    @Test
    public void beforeTest2()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        assertEquals(0, person.testInts(new int[] { 1, 2, 3 }).length);
    }
    
    @Test
    public void testOrder()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        assertEquals("3", person.order());
    }
    
    @Test
    public void testOrder2()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        assertEquals("你好", person.order2("林斌", 25));
    }
    
    @Test
    public void testMyname()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        assertEquals("林斌你好", person.myName("你好"));
    }
    
    @Test
    public void testThrow()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        try
        {
            person.throwe();
        }
        catch (Exception e)
        {
            assertEquals("aaaa", e.getMessage());
        }
    }
    
    @Test
    public void testTx()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        Person person = jfireContext.getBean(Person.class);
        person.tx();
        person.autoClose();
    }
    
    @Test
    public void testChildTx()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aop");
        ChildPerson person = jfireContext.getBean(ChildPerson.class);
        person.my();
    }
}
