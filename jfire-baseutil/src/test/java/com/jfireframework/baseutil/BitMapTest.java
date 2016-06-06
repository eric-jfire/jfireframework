package com.jfireframework.baseutil;

import static org.junit.Assert.*;
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
            assertTrue(bitMap.get(i));
        }
        for (int i = 11; i <= 20; i++)
        {
            assertFalse(bitMap.get(i));
        }
        
        for (int i = 21; i <= 30; i++)
        {
            assertTrue(bitMap.get(i));
        }
        bitMap.set(120);
        assertEquals(120, bitMap.max());
    }
    
    @Test
    public void test2()
    {
        BitMap bitMap = new BitMap();
        bitMap.set(50);
        bitMap.clear(50);
        assertEquals(0, bitMap.max());
    }
    
    @Test
    public void test3()
    {
        byte[] array = new byte[] { (byte) 0xd7, 0x12, 0x15, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1b };
        BitMap bitMap = BitMap.valueOf(array);
        assertTrue(bitMap.get(0));
        assertTrue(bitMap.get(1));
        assertFalse(bitMap.get(2));
        assertTrue(bitMap.get(3));
        assertFalse(bitMap.get(65));
        assertFalse(bitMap.get(66));
        assertTrue(bitMap.get(67));
        assertTrue(bitMap.get(68));
        assertFalse(bitMap.get(69));
        assertTrue(bitMap.get(70));
        assertTrue(bitMap.get(71));
    }
}
