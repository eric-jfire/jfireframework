package com.jfireframework.baseutil.collection;

import java.util.BitSet;

public class BitMap2
{
    private long[] array = new long[] { 0 };
    
    public boolean get(int i)
    {
        int wordIndex = i >>> 6;
        if (wordIndex >= array.length)
        {
            return false;
        }
        long value = 0x8000000000000000l >>> (i & 63);
        return (array[wordIndex] & value) != 0;
    }
    
    public void set(int i)
    {
        int wordIndex = i >>> 6;
        long value = 0x8000000000000000l >>> (i & 63);
        if (wordIndex >= array.length)
        {
            long[] tmp = new long[wordIndex + 1];
            System.arraycopy(array, 0, tmp, 0, array.length);
            array = tmp;
        }
        array[wordIndex] |= value;
    }
    
    public long count()
    {
        long sum = 0;
        for (long each : array)
        {
            sum += Long.bitCount(each);
        }
        return sum;
    }
    
    public void clear(int i)
    {
        int wordIndex = i >>> 6;
        long value = 0x8000000000000000l >>> (i & 63);
        if (wordIndex >= array.length)
        {
            long[] tmp = new long[wordIndex + 1];
            System.arraycopy(array, 0, tmp, 0, array.length);
            array = tmp;
        }
        array[wordIndex] &= ~value;
    }
    
    public int nextSetBit(int fromIndex)
    {
        int max = array.length * 64;
        for (int i = fromIndex; i < max; i++)
        {
            if (get(i))
            {
                return i;
            }
        }
        return -1;
    }
    
    public int nextClearBit(int fromIndex)
    {
        int wordIndex = fromIndex >>> 6;
        while (wordIndex < array.length)
        {
            if (array[wordIndex] == 0)
            {
                wordIndex += 1;
            }
        }
        int max = array.length * 64;
        for (int i = wordIndex * 64 + (fromIndex & 63); i < max; i++)
        {
            if (get(i) == false)
            {
                return i;
            }
        }
        return max;
    }
    
    public static void main(String[] args)
    {
        Long.bitCount(0);
        BitMap2 bitMap2 = new BitMap2();
        bitMap2.set(19);
        bitMap2.set(25);
        bitMap2.set(70);
        bitMap2.set(200);
        bitMap2.set(500);
        for (int i = 0; i < 1000; i++)
        {
            if (bitMap2.get(i))
            {
                System.out.println(i + ":" + bitMap2.get(i));
            }
        }
        System.out.println(bitMap2.nextSetBit(1));
        System.out.println(bitMap2.nextSetBit(20));
        System.out.println(bitMap2.nextSetBit(55));
        System.out.println(bitMap2.nextSetBit(100));
        System.out.println(bitMap2.nextSetBit(300));
    }
}
