package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
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
        readIndex = writeIndex = 0;
        capacity = array.length;
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
            buffer.get(memory, readIndex, length);
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
    
    @Override
    public void writeLength(int length)
    {
        if (length <= 251)
        {
            int newWriteIndex = writeIndex + 1;
            ensureCapacity(newWriteIndex);
            memory[writeIndex] = (byte) length;
            writeIndex = newWriteIndex;
        }
        else if (length <= 255)
        {
            int newWriteIndex = writeIndex + 2;
            ensureCapacity(newWriteIndex);
            memory[writeIndex] = (byte) 252;
            memory[writeIndex + 1] = (byte) length;
            writeIndex = newWriteIndex;
        }
        else if (length <= 0xffff)
        {
            int newWriteIndex = writeIndex + 3;
            ensureCapacity(newWriteIndex);
            memory[writeIndex] = (byte) 253;
            memory[writeIndex + 1] = (byte) (length >>> 8);
            memory[writeIndex + 2] = (byte) length;
            writeIndex = newWriteIndex;
        }
        
        else if (length <= 0xffffff)
        {
            int newWriteIndex = writeIndex + 4;
            ensureCapacity(newWriteIndex);
            memory[writeIndex] = (byte) 254;
            memory[writeIndex + 1] = (byte) (length >>> 16);
            memory[writeIndex + 2] = (byte) (length >>> 8);
            memory[writeIndex + 3] = (byte) length;
            writeIndex = newWriteIndex;
        }
        else
        {
            int newWriteIndex = writeIndex + 5;
            ensureCapacity(newWriteIndex);
            memory[writeIndex] = (byte) 255;
            memory[writeIndex + 1] = (byte) (length >>> 24);
            memory[writeIndex + 2] = (byte) (length >>> 16);
            memory[writeIndex + 3] = (byte) (length >>> 8);
            memory[writeIndex + 4] = (byte) length;
            writeIndex = newWriteIndex;
        }
    }
    
    @Override
    public int readLength()
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
}
