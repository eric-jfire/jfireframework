package com.jframework.licp.test.basetest;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.licp.util.IntUtil;

public class IntUtilTest
{
    @Test
    public void test()
    {
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++)
        {
            Assert.assertEquals(i, IntUtil.zag(IntUtil.zig(i)));
        }
    }
    
    @Test
    public void charTest()
    {
        int i = 'a';
        System.out.println(i);
    }
}
