package com.jfireframework.context.test.function.event;

import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.event.impl.IoEventPoster;

public class EventTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.event");
        jfireContext.addBean("com.jfireframework.context.event.impl.EventPosterImpl", false, IoEventPoster.class);
        jfireContext.initContext();
        
    }
}
