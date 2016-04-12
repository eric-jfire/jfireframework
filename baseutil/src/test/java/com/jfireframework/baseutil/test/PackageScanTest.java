package com.jfireframework.baseutil.test;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.PackageScan;

public class PackageScanTest
{
    @Test
    public void test()
    {
        String[] classNames = PackageScan.scan("com.jfireframework:out~*collection");
        for (String each : classNames)
        {
            System.out.println(each);
            Assert.assertFalse(each.startsWith("com.jfireframework.baseutil.collection"));
        }
        System.out.println("***************");
        classNames = PackageScan.scan("com.jfireframework:in~*collection");
        for (String each : classNames)
        {
            System.out.println(each);
            Assert.assertTrue(each.startsWith("com.jfireframework.baseutil.collection"));
        }
    }
}
