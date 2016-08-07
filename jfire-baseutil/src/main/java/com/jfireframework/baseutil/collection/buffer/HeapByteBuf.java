package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.verify.Verify;

public class HeapByteBuf extends ByteBuf<byte[]>
{
    
    public HeapByteBuf(byte[] array, Queue<byte[]> queue, Queue<ByteBuf<byte[]>> bufHost)
    {
        init(array, queue, bufHost);
    }
    
    public void init(byte[] array, Queue<byte[]> queue, Queue<ByteBuf<byte[]>> bufHost)
    {
        this.memory = array;
        this.bufHost = bufHost;
        maskRead = maskWrite = readIndex = writeIndex = 0;
        this.memHost = queue;
    }
    
    @Override
    protected void _release()
    {
    }
    
    protected void _expend(int size)
    {
        cachedNioBuffer = null;
        HeapByteBufPool.getInstance().expend(this, size);
        this.capacity = memory.length;
    }
    
    @Override
    protected void _put(ByteBuffer buffer, int length)
    {
        buffer.get(memory, writeIndex, length);
    }
    
    @Override
    protected void _put(int offset, byte b)
    {
        memory[offset] = b;
    }
    
    @Override
    protected void _put(byte[] content, int off, int len)
    {
        System.arraycopy(content, off, memory, writeIndex, len);
    }
    
    @Override
    public byte get(int index)
    {
        return memory[index];
    }
    
    @Override
    public byte get()
    {
        return memory[readIndex++];
    }
    
    @Override
    public byte[] toArray()
    {
        byte[] tmp = new byte[remainRead()];
        System.arraycopy(memory, readIndex, tmp, 0, tmp.length);
        return tmp;
    }
    
    @Override
    public HeapByteBuf compact()
    {
        if (readIndex != 0)
        {
            System.arraycopy(memory, readIndex, memory, 0, remainRead());
            writeIndex -= readIndex;
            readIndex = 0;
        }
        return this;
    }
    
    @Override
    protected void _get(byte[] content, int off, int length)
    {
        System.arraycopy(memory, readIndex, content, off, length);
        readIndex += length;
    }
    
    @Override
    public String toString(Charset charset, int length)
    {
        Verify.True(length <= remainRead(), "");
        return new String(memory, readIndex, readIndex + length, charset);
    }
    
    @Override
    public ByteBuffer nioBuffer()
    {
        return ByteBuffer.wrap(memory, readIndex, remainRead());
    }
    
    @Override
    protected void _put(ByteBuf<?> byteBuf, int length)
    {
        if (byteBuf instanceof HeapByteBuf)
        {
            System.arraycopy(byteBuf.memory, byteBuf.readIndex, memory, writeIndex, length);
        }
        else
        {
            ByteBuffer buffer = byteBuf.nioBuffer();
            int posi = buffer.position();
            buffer.get(memory, writeIndex, length);
            buffer.position(posi);
        }
    }
    
    @Override
    public void _writeInt(int index, int i)
    {
        memory[index] = (byte) (i >> 24);
        memory[index + 1] = (byte) (i >> 16);
        memory[index + 2] = (byte) (i >> 8);
        memory[index + 3] = (byte) (i);
    }
    
    @Override
    protected void _writeShort(int index, short s)
    {
        memory[index] = (byte) (s >> 8);
        memory[index + 1] = (byte) (s);
    }
    
    @Override
    protected void _writeLong(int index, long l)
    {
        memory[index] = (byte) (l >> 56);
        memory[index + 1] = (byte) (l >> 48);
        memory[index + 2] = (byte) (l >> 40);
        memory[index + 3] = (byte) (l >> 32);
        memory[index + 4] = (byte) (l >> 24);
        memory[index + 5] = (byte) (l >> 16);
        memory[index + 6] = (byte) (l >> 8);
        memory[index + 7] = (byte) (l);
    }
    
    @Override
    protected void _writeChar(int index, char c)
    {
        memory[index] = (byte) (c >> 8);
        memory[index + 1] = (byte) (c);
    }
    
    @Override
    protected void _writeBoolean(int index, boolean b)
    {
        if (b)
        {
            memory[index] = 0x01;
        }
        else
        {
            memory[index] = 0;
        }
    }
    
    @Override
    public int readInt()
    {
        int i = (memory[readIndex] & 0xff) << 24;
        i = i | (memory[readIndex + 1] & 0xff) << 16;
        i = i | (memory[readIndex + 2] & 0xff) << 8;
        i = i | (memory[readIndex + 3] & 0xff);
        readIndex += 4;
        return i;
    }
    
    @Override
    public short readShort()
    {
        short s = (short) ((memory[readIndex] & 0xff) << 8);
        s = (short) (s | (memory[readIndex + 1] & 0xff));
        readIndex += 2;
        return s;
    }
    
    @Override
    public long readLong()
    {
        long l = ((long) memory[readIndex] << 56) | (((long) memory[readIndex + 1] & 0xff) << 48) | (((long) memory[readIndex + 2] & 0xff) << 40) | (((long) memory[readIndex + 3] & 0xff) << 32) | (((long) memory[readIndex + 4] & 0xff) << 24) | (((long) memory[readIndex + 5] & 0xff) << 16) | (((long) memory[readIndex + 6] & 0xff) << 8) | (((long) memory[readIndex + 7] & 0xff));
        readIndex += 8;
        return l;
    }
    
    @Override
    public char readChar()
    {
        char c = (char) (memory[readIndex] << 8);
        c = (char) (c | (memory[readIndex + 1] & 0xff));
        readIndex += 2;
        return c;
    }
    
    @Override
    public float readFloat()
    {
        int i = readInt();
        return Float.intBitsToFloat(i);
    }
    
    @Override
    public double readDouble()
    {
        long l = readLong();
        return Double.longBitsToDouble(l);
    }
    
    @Override
    public boolean readBoolean()
    {
        if (memory[readIndex] == 0)
        {
            readIndex += 1;
            return false;
        }
        else
        {
            readIndex += 1;
            return true;
        }
    }
    
    @Override
    public int readInt(int index)
    {
        int i = (memory[index] & 0xff) << 24;
        i = i | (memory[index + 1] & 0xff) << 16;
        i = i | (memory[index + 2] & 0xff) << 8;
        i = i | memory[index + 3];
        return i;
    }
    
    @Override
    public short readShort(int index)
    {
        short s = (short) ((memory[index] & 0xff) << 8);
        s = (short) (s | memory[index + 1]);
        return s;
    }
    
    @Override
    public long readLong(int index)
    {
        long l = ((long) memory[readIndex] << 56) | (((long) memory[readIndex + 1] & 0xff) << 48) | (((long) memory[readIndex + 2] & 0xff) << 40) | (((long) memory[readIndex + 3] & 0xff) << 32) | (((long) memory[readIndex + 4] & 0xff) << 24) | (((long) memory[readIndex + 5] & 0xff) << 16) | (((long) memory[readIndex + 6] & 0xff) << 8) | (((long) memory[readIndex + 7] & 0xff));
        return l;
    }
    
    @Override
    public char readChar(int index)
    {
        char c = (char) (memory[index] << 8);
        c = (char) (c | memory[index + 1]);
        return c;
    }
    
    @Override
    public float readFloat(int index)
    {
        int i = readInt(index);
        return Float.intBitsToFloat(i);
    }
    
    @Override
    public double readDouble(int index)
    {
        long l = readLong(index);
        return Double.longBitsToDouble(l);
    }
    
    @Override
    public String hexString()
    {
        StringCache cache = new StringCache(remainRead());
        for (int i = readIndex; i < writeIndex; i++)
        {
            cache.append(DIGITS_LOWER[(memory[i] & 0xf0) >>> 4]);
            cache.append(DIGITS_LOWER[memory[i] & 0x0f]);
        }
        return cache.toString();
    }
    
    public void writePositive(int positive)
    {
        if (positive < 0)
        {
            throw new UnsupportedOperationException();
        }
        if (positive <= 251)
        {
            ensureCapacity(writeIndex + 1);
            memory[writeIndex] = (byte) positive;
            writeIndex += 1;
        }
        else if (positive <= 255)
        {
            ensureCapacity(writeIndex + 2);
            memory[writeIndex] = (byte) 252;
            memory[writeIndex + 1] = (byte) positive;
            writeIndex += 2;
        }
        else if (positive <= 0xffff)
        {
            ensureCapacity(writeIndex + 3);
            memory[writeIndex] = (byte) 253;
            memory[writeIndex + 1] = (byte) (positive >>> 8);
            memory[writeIndex + 2] = (byte) positive;
            writeIndex += 3;
        }
        else if (positive <= 0xffffff)
        {
            ensureCapacity(writeIndex + 4);
            memory[writeIndex] = (byte) 254;
            memory[writeIndex + 1] = (byte) (positive >>> 16);
            memory[writeIndex + 2] = (byte) (positive >>> 8);
            memory[writeIndex + 3] = (byte) positive;
            writeIndex += 4;
        }
        else
        {
            ensureCapacity(writeIndex + 5);
            memory[writeIndex] = (byte) 255;
            memory[writeIndex + 1] = (byte) (positive >>> 24);
            memory[writeIndex + 2] = (byte) (positive >>> 16);
            memory[writeIndex + 3] = (byte) (positive >>> 8);
            memory[writeIndex + 4] = (byte) positive;
            writeIndex += 5;
        }
    }
    
    public int readPositive()
    {
        int length = memory[readIndex++] & 0xff;
        if (length <= 251)
        {
            return length;
        }
        else if (length == 252)
        {
            length = memory[readIndex++] & 0xff;
            return length;
        }
        else if (length == 253)
        {
            length = (memory[readIndex++] & 0xff) << 8;
            length |= memory[readIndex++] & 0xff;
            return length;
        }
        else if (length == 254)
        {
            length = (memory[readIndex++] & 0xff) << 16;
            length |= (memory[readIndex++] & 0xff) << 8;
            length |= memory[readIndex++] & 0xff;
            return length;
        }
        else if (length == 255)
        {
            length = (memory[readIndex++] & 0xff) << 24;
            length |= (memory[readIndex++] & 0xff) << 16;
            length |= (memory[readIndex++] & 0xff) << 8;
            length |= memory[readIndex++] & 0xff;
            return length;
        }
        else
        {
            throw new RuntimeException("wrong data");
        }
    }
    
    public static HeapByteBuf allocate(int size)
    {
        return HeapByteBufPool.getInstance().get(size);
    }
    
    public static HeapByteBuf allocateWithTrace(int size)
    {
        HeapByteBuf buf = HeapByteBufPool.getInstance().get(size);
        buf.setTraceFlag(true);
        return buf;
    }
    
    @Override
    public HeapByteBuf writeVarint(int i)
    {
        if (i >= -120 && i <= 127)
        {
            ensureCapacity(writeIndex + 1);
            memory[writeIndex] = (byte) i;
            writeIndex += 1;
            return this;
        }
        int head = -120;
        if (i < 0)
        {
            i = ~i;
            head = -124;
        }
        if (i <= 0x000000ff)
        {
            ensureCapacity(writeIndex + 2);
            memory[writeIndex] = (byte) (head - 1);
            memory[writeIndex + 1] = (byte) i;
            writeIndex += 2;
        }
        else if (i <= 0x0000ffff)
        {
            ensureCapacity(writeIndex + 3);
            memory[writeIndex] = (byte) (head - 2);
            memory[writeIndex + 1] = (byte) (i >>> 8);
            memory[writeIndex + 2] = (byte) i;
            writeIndex += 3;
        }
        else if (i <= 0x00ffffff)
        {
            ensureCapacity(writeIndex + 4);
            memory[writeIndex] = (byte) (head - 3);
            memory[writeIndex + 1] = (byte) (i >>> 16);
            memory[writeIndex + 2] = (byte) (i >>> 8);
            memory[writeIndex + 3] = (byte) i;
            writeIndex += 4;
        }
        else
        {
            ensureCapacity(writeIndex + 5);
            memory[writeIndex] = (byte) (head - 4);
            memory[writeIndex + 1] = (byte) (i >>> 24);
            memory[writeIndex + 2] = (byte) (i >>> 16);
            memory[writeIndex + 3] = (byte) (i >>> 8);
            memory[writeIndex + 4] = (byte) i;
            writeIndex += 5;
        }
        return this;
    }
    
    @Override
    public int readVarint()
    {
        byte b = memory[readIndex++];
        if (b >= -120 && b <= 127)
        {
            return b;
        }
        switch (b)
        {
            case -121:
                return memory[readIndex++] & 0xff;
            case -122:
                return ((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff);
            case -123:
                return ((memory[readIndex++] & 0xff) << 16) | ((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff);
            case -124:
                return ((memory[readIndex++] & 0xff) << 24) | ((memory[readIndex++] & 0xff) << 16) | ((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff);
            case -125:
                return ~(memory[readIndex++] & 0xff);
            case -126:
                return ~(((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff));
            case -127:
                return ~(((memory[readIndex++] & 0xff) << 16) | ((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff));
            case -128:
                return ~(((memory[readIndex++] & 0xff) << 24) | ((memory[readIndex++] & 0xff) << 16) | ((memory[readIndex++] & 0xff) << 8) | (memory[readIndex++] & 0xff));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    public HeapByteBuf writeVarLong(long i)
    {
        if (i >= -112 && i <= 127)
        {
            ensureCapacity(writeIndex + 1);
            memory[writeIndex] = (byte) i;
            writeIndex += 1;
            return this;
        }
        int head = -112;
        if (i < 0)
        {
            i = ~i;
            head = -120;
        }
        if (i <= 0x00000000000000ff)
        {
            ensureCapacity(writeIndex + 2);
            memory[writeIndex] = (byte) (head - 1);
            memory[writeIndex + 1] = (byte) i;
            writeIndex += 2;
        }
        else if (i <= 0x000000000000ffff)
        {
            ensureCapacity(writeIndex + 3);
            memory[writeIndex] = (byte) (head - 2);
            memory[writeIndex + 1] = (byte) (i >>> 8);
            memory[writeIndex + 2] = (byte) i;
            writeIndex += 3;
        }
        else if (i <= 0x0000000000ffffff)
        {
            ensureCapacity(writeIndex + 4);
            memory[writeIndex] = (byte) (head - 3);
            memory[writeIndex + 1] = (byte) (i >>> 16);
            memory[writeIndex + 2] = (byte) (i >>> 8);
            memory[writeIndex + 3] = (byte) i;
            writeIndex += 4;
        }
        else if (i <= 0x00000000ffffffff)
        {
            ensureCapacity(writeIndex + 5);
            memory[writeIndex] = (byte) (head - 4);
            memory[writeIndex + 1] = (byte) (i >>> 24);
            memory[writeIndex + 2] = (byte) (i >>> 16);
            memory[writeIndex + 3] = (byte) (i >>> 8);
            memory[writeIndex + 4] = (byte) i;
            writeIndex += 5;
        }
        else if (i <= 0x000000ffffffffffl)
        {
            ensureCapacity(writeIndex + 6);
            memory[writeIndex] = (byte) (head - 5);
            memory[writeIndex + 1] = (byte) (i >>> 32);
            memory[writeIndex + 2] = (byte) (i >>> 24);
            memory[writeIndex + 3] = (byte) (i >>> 16);
            memory[writeIndex + 4] = (byte) (i >>> 8);
            memory[writeIndex + 5] = (byte) i;
            writeIndex += 6;
        }
        else if (i <= 0x0000ffffffffffffl)
        {
            ensureCapacity(writeIndex + 7);
            memory[writeIndex] = (byte) (head - 6);
            memory[writeIndex + 1] = (byte) (i >>> 40);
            memory[writeIndex + 2] = (byte) (i >>> 32);
            memory[writeIndex + 3] = (byte) (i >>> 24);
            memory[writeIndex + 4] = (byte) (i >>> 16);
            memory[writeIndex + 5] = (byte) (i >>> 8);
            memory[writeIndex + 6] = (byte) i;
            writeIndex += 7;
        }
        else if (i <= 0x00ffffffffffffffl)
        {
            ensureCapacity(writeIndex + 8);
            memory[writeIndex] = (byte) (head - 7);
            memory[writeIndex + 1] = (byte) (i >>> 48);
            memory[writeIndex + 2] = (byte) (i >>> 40);
            memory[writeIndex + 3] = (byte) (i >>> 32);
            memory[writeIndex + 4] = (byte) (i >>> 24);
            memory[writeIndex + 5] = (byte) (i >>> 16);
            memory[writeIndex + 6] = (byte) (i >>> 8);
            memory[writeIndex + 7] = (byte) i;
            writeIndex += 8;
        }
        else
        {
            ensureCapacity(writeIndex + 9);
            memory[writeIndex] = (byte) (head - 8);
            memory[writeIndex + 1] = (byte) (i >>> 56);
            memory[writeIndex + 2] = (byte) (i >>> 48);
            memory[writeIndex + 3] = (byte) (i >>> 40);
            memory[writeIndex + 4] = (byte) (i >>> 32);
            memory[writeIndex + 5] = (byte) (i >>> 24);
            memory[writeIndex + 6] = (byte) (i >>> 16);
            memory[writeIndex + 7] = (byte) (i >>> 8);
            memory[writeIndex + 8] = (byte) i;
            writeIndex += 9;
        }
        return this;
    }
    
    public long readVarLong()
    {
        byte b = memory[readIndex++];
        if (b >= -112 && b <= 127)
        {
            return b;
        }
        switch (b)
        {
            case -113:
                return memory[readIndex++] & 0xffl;
            case -114:
                return ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -115:
                return ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -116:
                return ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -117:
                return ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -118:
                return ((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -119:
                return ((memory[readIndex++] & 0xffl) << 48) | ((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -120:
                return ((memory[readIndex++] & 0xffl) << 56) | ((memory[readIndex++] & 0xffl) << 48) | ((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl);
            case -121:
                return ~(memory[readIndex++] & 0xffl);
            case -122:
                return ~(((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -123:
                return ~(((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -124:
                return ~(((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -125:
                return ~(((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -126:
                return ~(((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -127:
                return ~(((memory[readIndex++] & 0xffl) << 48) | ((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            case -128:
                return ~(((memory[readIndex++] & 0xffl) << 56) | ((memory[readIndex++] & 0xffl) << 48) | ((memory[readIndex++] & 0xffl) << 40) | ((memory[readIndex++] & 0xffl) << 32) | ((memory[readIndex++] & 0xffl) << 24) | ((memory[readIndex++] & 0xffl) << 16) | ((memory[readIndex++] & 0xffl) << 8) | (memory[readIndex++] & 0xffl));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    @Override
    public HeapByteBuf writeVarChar(char c)
    {
        ensureCapacity(writeIndex + 3);
        return _writeVarChar(c);
    }
    
    private HeapByteBuf _writeVarChar(char c)
    {
        int positive = c;
        if (positive <= 251)
        {
            memory[writeIndex] = (byte) positive;
            writeIndex += 1;
        }
        else if (positive <= 255)
        {
            memory[writeIndex] = (byte) 252;
            memory[writeIndex + 1] = (byte) positive;
            writeIndex += 2;
        }
        else if (positive <= 0xffff)
        {
            memory[writeIndex] = (byte) 253;
            memory[writeIndex + 1] = (byte) (positive >>> 8);
            memory[writeIndex + 2] = (byte) positive;
            writeIndex += 3;
        }
        return this;
    }
    
    @Override
    public char readVarChar()
    {
        int length = memory[readIndex++] & 0xff;
        if (length <= 251)
        {
            return (char) length;
        }
        else if (length == 252)
        {
            length = memory[readIndex++] & 0xff;
            return (char) length;
        }
        else if (length == 253)
        {
            length = (memory[readIndex++] & 0xff) << 8;
            length |= memory[readIndex++] & 0xff;
            return (char) length;
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    @Override
    public HeapByteBuf writeString(String value)
    {
        if (value == null)
        {
            throw new NullPointerException();
        }
        int length = value.length();
        writePositive(length);
        ensureCapacity(writeIndex + length * 3);
        for (int i = 0; i < length; i++)
        {
            _writeVarChar(value.charAt(i));
        }
        return this;
        
    }
    
    @Override
    public String readString()
    {
        int length = readPositive();
        if (length == 0)
        {
            return "";
        }
        char[] src = new char[length];
        for (int i = 0; i < length; i++)
        {
            src[i] = readVarChar();
        }
        return new String(src);
    }
}
