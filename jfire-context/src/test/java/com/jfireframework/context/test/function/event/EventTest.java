package com.jfireframework.context.test.function.event;

import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.BeanConfig;

public class EventTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.event");
        BeanConfig beanConfig = new BeanConfig("eventPublisher");
        beanConfig.putParam("capacity", "1024");
        jfireContext.addBeanConfig(beanConfig);
        jfireContext.initContext();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
