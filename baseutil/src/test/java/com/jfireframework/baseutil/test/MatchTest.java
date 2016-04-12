package com.jfireframework.baseutil.test;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.StringUtil;

public class MatchTest
{
    @Test
    public void test()
    {
        Assert.assertTrue(StringUtil.match("sada.sda.test", "*test"));
        Assert.assertTrue(StringUtil.match("com.sda.sdas", "*"));
        Assert.assertTrue(StringUtil.match("/user/12/getid/121", "/user/*/getid/*"));
        Assert.assertFalse(StringUtil.match("/user/12/getid/121/123/456", "/user/*/getid/*/123"));
    }
}
