package com.jfireframework.baseutil.test;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.StringUtil;

public class StringTest
{
    @Test
    public void test()
    {
        String pattern = "这是一个很大的问题，问题是{},zenm{}";
        String result = StringUtil.format(pattern, "嘿嘿", 1);
        Assert.assertEquals("这是一个很大的问题，问题是嘿嘿,zenm1", result);
        pattern = "这是一个很大的问题，问题是{},zenm{},21asda{}";
        result = StringUtil.format(pattern, "嘿嘿", 1);
        Assert.assertEquals("这是一个很大的问题，问题是嘿嘿,zenm1,21asda{}", result);
    }
}
