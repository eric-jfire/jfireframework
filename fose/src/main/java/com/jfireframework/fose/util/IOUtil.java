package com.jfireframework.fose.util;

import com.jfireframework.baseutil.collection.ByteCache;

public class IOUtil
{
    private static int[] offsets = new int[] { 0, 8, 16, 24, 32, 40, 48, 56 };
    
    public static void writeInt(int num, ByteCache cache)
    {
        cache.ensureLeft(5);
        writeIntWithoutCheck(num, cache);
    }
    
    private static void writeIntWithoutCheck(int num, ByteCache cache)
    {
        if (num >= -120 && num <= 127)
        {
            cache.putWithoutCheck((byte) num);
            return;
        }
        int length = 0;
        if (num > 0)
        {
            byte first = -120;
            int tmp = num;
            while (tmp != 0)
            {
                tmp = tmp >> 8;
                first--;
                length++;
            }
            cache.put(first);
            for (int j = 0; j < length; j++)
            {
                cache.putWithoutCheck((byte) num);
                num = num >> 8;
            }
        }
        else
        {
            byte first = -124;
            int tmp = num;
            while (tmp != -1)
            {
                tmp = tmp >> 8;
                first--;
                length++;
            }
            cache.put(first);
            for (int j = 0; j < length; j++)
            {
                cache.putWithoutCheck((byte) num);
                num = num >> 8;
            }
        }
    }
    
    public static int readInt(ByteCache cache)
    {
        byte head = cache.get();
        if (head >= -120 && head <= 127)
        {
            return head;
        }
        else
        {
            int length = head >= -124 ? -head - 120 : -head - 124;
            int out = 0;
            for (int i = 0; i < length; i++)
            {
                out |= (cache.get() & 0xff) << offsets[i];
            }
            if (length == 4 || head >= -124)
            {
                return out;
            }
            if (head < -124)
            {
                switch (length)
                {
                    case 3:
                    {
                        out |= 0xff000000;
                        return out;
                    }
                    case 2:
                    {
                        out |= 0xffff0000;
                        return out;
                    }
                    case 1:
                    {
                        out |= 0xffffff00;
                        return out;
                    }
                }
            }
        }
        throw new RuntimeException("");
    }
    
    public static void writeIntArray(int[] array, ByteCache cache)
    {
        int length = array.length;
        cache.ensureLeft(length * 5);
        for (int i = 0; i < length; i++)
        {
            IOUtil.writeIntWithoutCheck(array[i], cache);
        }
    }
    
    public static void writeLong(long num, ByteCache cache)
    {
        cache.ensureLeft(9);
        writeLongWithoutCheck(num, cache);
    }
    
    private static void writeLongWithoutCheck(long num, ByteCache cache)
    {
        if (num >= -112 && num <= 127)
        {
            cache.putWithoutCheck((byte) num);
            return;
        }
        int length = 0;
        byte first = -112;
        if (num < 0)
        {
            first = -120;
        }
        long tmp = num;
        while (tmp != 0)
        {
            tmp = tmp >>> 8;
            first--;
            length++;
        }
        cache.putWithoutCheck(first);
        for (int j = 0; j < length; j++)
        {
            cache.putWithoutCheck((byte) num);
            num = num >>> 8;
        }
    }
    
    public static void writeLongArray(long[] array, ByteCache cache)
    {
        int length = array.length;
        cache.ensureLeft(length * 9);
        for (int i = 0; i < length; i++)
        {
            writeLongWithoutCheck(array[i], cache);
        }
    }
    
    public static void writeFloat(float num, ByteCache cache)
    {
        cache.ensureLeft(5);
        int tmp = Float.floatToRawIntBits(num);
        for (int i = 0; i < 4; i++)
        {
            cache.putWithoutCheck((byte) tmp);
            tmp = tmp >>> 8;
        }
    }
    
    public static long readLong(ByteCache buffer)
    {
        byte head = buffer.get();
        if (head >= -112 && head <= 127)
        {
            return head;
        }
        else
        {
            int length = head >= -124 ? (-head - 112) : (-head - 120);
            long out = 0;
            for (int i = 0; i < length; i++)
            {
                out |= (buffer.get() & 0xffL) << offsets[i];
            }
            return out;
        }
    }
    
    public static void writeFloatArray(float[] array, ByteCache cache)
    {
        int length = array.length;
        cache.ensureLeft(length * 5);
        for (int j = 0; j < length; j++)
        {
            int tmp = Float.floatToRawIntBits(array[j]);
            for (int i = 0; i < 4; i++)
            {
                cache.putWithoutCheck((byte) (tmp >> 8 * i & 0xFF));
            }
        }
    }
    
    public static float readFloat(ByteCache buffer)
    {
        int tmp = 0x00;
        tmp |= buffer.get() & 0xff;
        tmp |= (buffer.get() & 0xff) << 8;
        tmp |= (buffer.get() & 0xff) << 16;
        tmp |= (buffer.get() & 0xff) << 24;
        return Float.intBitsToFloat(tmp);
    }
    
    public static void writeDouble(double num, ByteCache buffer)
    {
        buffer.ensureLeft(9);
        long tmp = Double.doubleToRawLongBits(num);
        for (int i = 0; i < 8; i++)
        {
            buffer.putWithoutCheck((byte) tmp);
            tmp = tmp >>> 8;
        }
    }
    
    public static void writeDoubleArray(double[] array, ByteCache cache)
    {
        int length = array.length;
        cache.ensureLeft(length * 9);
        for (int i = 0; i < length; i++)
        {
            long tmp = Double.doubleToRawLongBits(array[i]);
            for (int j = 0; j < 8; j++)
            {
                cache.putWithoutCheck((byte) (tmp >> 8 * i & 0xFF));
            }
        }
    }
    
    public static double readDouble(ByteCache buffer)
    {
        long tmp = ((buffer.get() & 0xffl))
                | (buffer.get() & 0xffl) << 8
                | (buffer.get() & 0xffl) << 16
                | (buffer.get() & 0xffl) << 24
                | (buffer.get() & 0xffl) << 32
                | (buffer.get() & 0xffl) << 40
                | (buffer.get() & 0xffl) << 48
                | (buffer.get() & 0xffl) << 56;
        return Double.longBitsToDouble(tmp);
    }
    
    public static void writeChar(char c, ByteCache cache)
    {
        cache.ensureLeft(2);
        cache.putWithoutCheck((byte) c);
        cache.putWithoutCheck((byte) (c >> 8));
    }
    
    public static void writeCharArray(char[] c, ByteCache cache)
    {
        int length = c.length;
        cache.ensureLeft(length * 2);
        for (int i = 0; i < length; i++)
        {
            cache.putWithoutCheck((byte) c[i]);
            cache.putWithoutCheck((byte) (c[i] >> 8));
        }
    }
    
    public static char readChar(ByteCache cache)
    {
        byte bit0 = cache.get();
        byte bit1 = cache.get();
        return (char) ((((char) bit1) << 8) | ((char) bit0 & 0x00ff));
    }
    
    public static void writeShort(short s, ByteCache cache)
    {
        cache.ensureLeft(2);
        cache.putWithoutCheck((byte) s);
        cache.putWithoutCheck((byte) (s >> 8));
    }
    
    public static void writeShortArray(short[] array, ByteCache cache)
    {
        int length = array.length;
        cache.ensureLeft(length * 2);
        for (int i = 0; i < length; i++)
        {
            short s = array[i];
            cache.putWithoutCheck((byte) s);
            cache.putWithoutCheck((byte) (s >> 8));
        }
    }
    
    public static short readShort(ByteCache cache)
    {
        byte bit0 = cache.get();
        byte bit1 = cache.get();
        return (short) ((((short) bit1) << 8) | ((short) bit0 & 0x00ff));
    }
}
