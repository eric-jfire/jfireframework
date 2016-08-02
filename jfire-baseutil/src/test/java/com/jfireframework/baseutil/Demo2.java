package com.jfireframework.baseutil;

import org.junit.Assert;
import org.junit.Test;

public class Demo2
{
    @Test
    public void test()
    {
        for (long l = 0; l < 1000800; l++)
        {
            Assert.assertEquals(((int) l) & 1, (int) (l & 1));
        }
    }
}
