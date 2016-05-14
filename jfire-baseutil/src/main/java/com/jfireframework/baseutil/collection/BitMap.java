package com.jfireframework.baseutil.collection;

public class BitMap
{
    private byte[] array = new byte[1];
    
    private void setArray(byte[] array)
    {
        this.array = array;
    }
    
    public static BitMap valueOf(byte[] array)
    {
        BitMap bitMap = new BitMap();
        bitMap.setArray(array);
        return bitMap;
    }
    
    /**
     * 获取当前bitmap中最大的数字
     * 
     * @return
     */
    public int max()
    {
        int flag = array.length - 1;
        while (flag > -1)
        {
            int head = (flag << 3) - 1;
            byte value = array[flag];
            if (value == 0)
            {
                flag -= 1;
                continue;
            }
            switch (value)
            {
                case (byte) 0xff:
                    return head + 8;
                case (byte) 0xfe:
                    return head + 7;
                case (byte) 0xfc:
                    return head + 6;
                case (byte) 0xf8:
                    return head + 5;
                case (byte) 0xf0:
                    return head + 4;
                case (byte) 0xe0:
                    return head + 3;
                case (byte) 0xc0:
                    return head + 2;
                case (byte) 0x80:
                    return head + 1;
                default:
                    throw new RuntimeException("error");
            }
        }
        return 0;
    }
    
    public boolean get(int i)
    {
        int wordIndex = i >>> 3;
        if (wordIndex >= array.length)
        {
            return false;
        }
        byte value = (byte) (0x80 >>> (i & 7));
        return (array[wordIndex] & value) != 0;
    }
    
    public void set(int i)
    {
        int wordIndex = i >>> 3;
        byte value = (byte) (0x80 >>> (i & 7));
        if (wordIndex >= array.length)
        {
            byte[] tmp = new byte[wordIndex + 1];
            System.arraycopy(array, 0, tmp, 0, array.length);
            array = tmp;
        }
        array[wordIndex] |= value;
    }
    
    public void clear(int i)
    {
        int wordIndex = i >>> 3;
        byte value = (byte) (0x80 >>> (i & 7));
        if (wordIndex >= array.length)
        {
            byte[] tmp = new byte[wordIndex + 1];
            System.arraycopy(array, 0, tmp, 0, array.length);
            array = tmp;
        }
        array[wordIndex] &= ~value;
    }
    
    public int nextSetBit(int fromIndex)
    {
        int max = array.length * 8;
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
        int max = array.length * 8;
        for (int i = fromIndex; i < max; i++)
        {
            if (get(i) == false)
            {
                return i;
            }
        }
        return max;
    }
}
