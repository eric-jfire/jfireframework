package com.jfireframework.licp.util;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.exception.UnSupportException;

public class BufferUtil
{
    public static boolean readBoolean(ByteBuffer buffer)
    {
        if (buffer.get() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public static String readString(ByteBuffer buffer)
    {
        int length = readPositive(buffer);
        if (length == 0)
        {
            return "";
        }
        char[] src = new char[length];
        for (int i = 0; i < length; i++)
        {
            src[i] = readVarChar(buffer);
        }
        return new String(src);
    }
    
    public static char readChar(ByteBuffer buffer)
    {
        char c = (char) (buffer.get() << 8);
        c = (char) (c | (buffer.get() & 0xff));
        return c;
    }
    
    public static short readShort(ByteBuffer buffer)
    {
        short s = (short) ((buffer.get() & 0xff) << 8);
        s = (short) (s | (buffer.get() & 0xff));
        return s;
    }
    
    public static int readPositive(ByteBuffer buffer)
    {
        int length = buffer.get() & 0xff;
        if (length <= 251)
        {
            return length;
        }
        else if (length == 252)
        {
            length = buffer.get() & 0xff;
            return length;
        }
        else if (length == 253)
        {
            length = (buffer.get() & 0xff) << 8;
            length |= buffer.get() & 0xff;
            return length;
        }
        else if (length == 254)
        {
            length = (buffer.get() & 0xff) << 16;
            length |= (buffer.get() & 0xff) << 8;
            length |= buffer.get() & 0xff;
            return length;
        }
        else if (length == 255)
        {
            length = (buffer.get() & 0xff) << 24;
            length |= (buffer.get() & 0xff) << 16;
            length |= (buffer.get() & 0xff) << 8;
            length |= buffer.get() & 0xff;
            return length;
        }
        else
        {
            throw new RuntimeException("wrong data");
        }
    }
    
    public static char readVarChar(ByteBuffer buffer)
    {
        int length = buffer.get() & 0xff;
        if (length <= 251)
        {
            return (char) length;
        }
        else if (length == 252)
        {
            length = buffer.get() & 0xff;
            return (char) length;
        }
        else if (length == 253)
        {
            length = (buffer.get() & 0xff) << 8;
            length |= buffer.get() & 0xff;
            return (char) length;
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    public static long readLong(ByteBuffer buffer)
    {
        long l = ((long) buffer.get() << 56) | (((long) buffer.get() & 0xff) << 48) | (((long) buffer.get() & 0xff) << 40) | (((long) buffer.get() & 0xff) << 32) | (((long) buffer.get() & 0xff) << 24) | (((long) buffer.get() & 0xff) << 16) | (((long) buffer.get() & 0xff) << 8) | ((buffer.get() & 0xff));
        return l;
    }
    
    public static double readDouble(ByteBuffer buffer)
    {
        long l = readLong(buffer);
        return Double.longBitsToDouble(l);
    }
    
    public static int readInt(ByteBuffer buffer)
    {
        int i = (buffer.get() & 0xff) << 24;
        i = i | (buffer.get() & 0xff) << 16;
        i = i | (buffer.get() & 0xff) << 8;
        i = i | (buffer.get() & 0xff);
        return i;
    }
    
    public static float readFloat(ByteBuffer buffer)
    {
        int i = readInt(buffer);
        float f = Float.intBitsToFloat(i);
        return f;
    }
    
    public static int readVarint(ByteBuffer buffer)
    {
        byte b = buffer.get();
        if (b >= -120 && b <= 127)
        {
            return b;
        }
        switch (b)
        {
            case -121:
                return buffer.get() & 0xff;
            case -122:
                return ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
            case -123:
                return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
            case -124:
                return ((buffer.get() & 0xff) << 24) | ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
            case -125:
                return ~(buffer.get() & 0xff);
            case -126:
                return ~(((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff));
            case -127:
                return ~(((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff));
            case -128:
                return ~(((buffer.get() & 0xff) << 24) | ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    public static long readVarLong(ByteBuffer buffer)
    {
        byte b = buffer.get();
        if (b >= -112 && b <= 127)
        {
            return b;
        }
        switch (b)
        {
            case -113:
                return buffer.get() & 0xffl;
            case -114:
                return ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -115:
                return ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -116:
                return ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -117:
                return ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -118:
                return ((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -119:
                return ((buffer.get() & 0xffl) << 48) | ((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -120:
                return ((buffer.get() & 0xffl) << 56) | ((buffer.get() & 0xffl) << 48) | ((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl);
            case -121:
                return ~(buffer.get() & 0xffl);
            case -122:
                return ~(((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -123:
                return ~(((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -124:
                return ~(((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -125:
                return ~(((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -126:
                return ~(((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -127:
                return ~(((buffer.get() & 0xffl) << 48) | ((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            case -128:
                return ~(((buffer.get() & 0xffl) << 56) | ((buffer.get() & 0xffl) << 48) | ((buffer.get() & 0xffl) << 40) | ((buffer.get() & 0xffl) << 32) | ((buffer.get() & 0xffl) << 24) | ((buffer.get() & 0xffl) << 16) | ((buffer.get() & 0xffl) << 8) | (buffer.get() & 0xffl));
            default:
                throw new UnSupportException("not here");
        }
    }
}
