package com.jfireframework.context.test.function.map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class MapTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.map");
        jfireContext.initContext();
        assertEquals(jfireContext.getBean(Host.class).getMap().get(1).getClass(), Order1.class);
        assertEquals(2, jfireContext.getBean(Host.class).getMap().size());
        assertEquals(2, jfireContext.getBean(Host.class).getMap2().size());
        assertEquals(jfireContext.getBean(Host.class).getMap2().get(Order1.class.getName()).getClass(), Order1.class);
    }
    
}
