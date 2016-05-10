package com.jfireframework.baseutil;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.collection.BitMap;

public class BitMapTest
{
    @Test
    public void test()
    {
        BitMap bitMap = new BitMap();
        for (int i = 1; i <= 10; i++)
        {
            bitMap.set(i);
        }
        for (int i = 21; i <= 30; i++)
        {
            bitMap.set(i);
        }
        for (int i = 1; i <= 10; i++)
        {
            Assert.assertTrue(bitMap.get(i));
        }
        for (int i = 11; i <= 20; i++)
        {
            Assert.assertFalse(bitMap.get(i));
        }
        
        for (int i = 21; i <= 30; i++)
        {
            Assert.assertTrue(bitMap.get(i));
        }
        bitMap.set(120);
        Assert.assertEquals(120, bitMap.max());
    }
}
