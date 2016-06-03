package com.jfireframework.baseutil.collection;

public class BitMap
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
    
    public int count()
    {
        int sum = 0;
        for (long each : array)
        {
            sum += Long.bitCount(each);
        }
        return sum;
    }
    
    public int max()
    {
        int index = array.length - 1;
        while (index != -1)
        {
            if (array[index] != 0)
            {
                for (int i = (index + 1) << 6, max = index << 6; i >= max; i--)
                {
                    if (get(i))
                    {
                        return i;
                    }
                }
            }
            index -= 1;
        }
        return 0;
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
        int wordIndex = fromIndex >>> 6;
        while (wordIndex < array.length)
        {
            if (array[wordIndex] == 0)
            {
                wordIndex += 1;
            }
            else
            {
                break;
            }
        }
        int max = array.length * 64;
        for (int i = wordIndex * 64 + (fromIndex & 63); i < max; i++)
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
            else
            {
                break;
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
    
}
