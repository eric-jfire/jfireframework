package com.jfireframework.context.test.function.event;

import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.event.impl.EventPosterImpl;

public class EventTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.event");
        BeanConfig beanConfig = new BeanConfig("com.jfireframework.context.event.impl.EventPosterImpl");
        beanConfig.putParam("capacity", "1024");
        jfireContext.addBeanConfig(beanConfig);
        jfireContext.addBean("com.jfireframework.context.event.impl.EventPosterImpl", false, EventPosterImpl.class);
        jfireContext.initContext();
        
    }
}
