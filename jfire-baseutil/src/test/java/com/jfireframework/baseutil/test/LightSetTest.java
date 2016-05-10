package com.jfireframework.baseutil.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.jfireframework.baseutil.collection.set.LightSet;

public class LightSetTest
{
    @Test
    public void test()
    {
        LightSet<String> lightSet = new LightSet<String>();
        String[] data = new String[] { "sd32dase12", "dae32qfde3", "2edssaerw" };
        int index = 0;
        for (String each : lightSet)
        {
            assertEquals(each, data[index]);
        }
    }
    
    @Test
    public void testtyepe()
    {
        LightSet<String> set = new LightSet<String>();
        System.out.println(set.getClass().getGenericSuperclass());
    }
    
    @Test
    public void test2()
    {
        LightSet<String> lightset = new LightSet<String>();
        lightset.add("sdasda");
        lightset.removeValue("sdasda");
        for (String each : lightset)
        {
            fail(each);
        }
        lightset.add("sdasda");
        assertEquals("sdasda", lightset.getHead().next().value());
    }
}
