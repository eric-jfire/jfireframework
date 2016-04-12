package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class DirectByteBuf extends ByteBuf<ByteBuffer>
{
    
    private static long   offset = ReflectUtil.getFieldOffset("cleaner", ByteBuffer.allocateDirect(0).getClass());
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
                                 
    public DirectByteBuf(ByteBuffer memory, Queue<ByteBuffer> host)
    {
        this.memory = memory;
        this.host = host;
        readIndex = 0;
        writeIndex = 0;
        capacity = memory.capacity();
    }
    
    @Override
    protected void _release()
    {
        // 执行对directBytebuffer的清理。否则由于该对象个头很小，可能导致堆外内存无法被回收。
        Object cleaner = unsafe.getObject(memory, offset);
        ((sun.misc.Cleaner) cleaner).clean();
    }
    
    /**
     * 将position设置为writeIndex，将limit设置为容量上限
     */
    protected DirectByteBuf changeToWriteState()
    {
        memory.limit(capacity).position(writeIndex);
        return this;
    }
    
    /**
     * 将position设置为readIndex，将limit设置为writeIndex
     */
    protected DirectByteBuf changeToReadState()
    {
        if (memory == null)
        {
            throw new RuntimeException("数据已经被释放,方式信息:" + releaseInfo);
        }
        // 先limit后postion，否则有些情况，会因为长度不一致，抛出错误
        memory.limit(writeIndex).position(readIndex);
        return this;
    }
    
    @Override
    protected void _put(ByteBuffer buffer, int length)
    {
        changeToWriteState();
        int limit = buffer.limit();
        buffer.limit(buffer.position() + length);
        memory.put(buffer);
        buffer.limit(limit);
    }
    
    @Override
    protected void _put(int offset, byte b)
    {
        changeToWriteState();
        memory.put(offset, b);
    }
    
    @Override
    protected void _put(byte[] content, int off, int len)
    {
        changeToWriteState();
        memory.put(content, off, len);
    }
    
    @Override
    public byte get(int index)
    {
        return memory.get(index);
    }
    
    @Override
    public byte get()
    {
        changeToReadState();
        byte result = memory.get();
        readIndex++;
        return result;
    }
    
    @Override
    public byte[] toArray()
    {
        changeToReadState();
        byte[] tmp = new byte[remainRead()];
        memory.get(tmp);
        readIndex = writeIndex;
        return tmp;
    }
    
    @Override
    public DirectByteBuf compact()
    {
        if (readIndex != 0)
        {
            changeToReadState();
            memory.compact();
            writeIndex -= readIndex;
            readIndex = 0;
        }
        return this;
    }
    
    @Override
    protected void _get(byte[] content, int off, int length)
    {
        changeToReadState();
        memory.get(content, off, length);
        readIndex += length;
    }
    
    @Override
    public String toString(Charset charset, int length)
    {
        Verify.True(length <= remainRead(), "");
        byte[] src = new byte[length];
        changeToReadState();
        memory.get(src);
        return new String(src, charset);
    }
    
    @Override
    protected void _expend(int size)
    {
        DirectByteBufPool.getInstance().expend(this, size);
        this.capacity = memory.capacity();
    }
    
    @Override
    public ByteBuffer nioBuffer()
    {
        return changeToReadState().memory;
    }
    
    @Override
    protected void _put(ByteBuf<?> byteBuf, int length)
    {
        changeToWriteState();
        if (byteBuf instanceof HeapByteBuf)
        {
            memory.put((byte[]) byteBuf.memory, byteBuf.readIndex, length);
        }
        else
        {
            ByteBuffer buffer = byteBuf.nioBuffer();
            buffer.limit(buffer.position() + length);
            memory.put(buffer);
        }
    }
    
    @Override
    public void _writeInt(int index, int i)
    {
        changeToWriteState();
        memory.putInt(index, i);
    }
    
    @Override
    protected void _writeShort(int index, short s)
    {
        changeToWriteState();
        memory.putShort(index, s);
    }
    
    @Override
    protected void _writeLong(int index, long l)
    {
        changeToWriteState();
        memory.putLong(index, l);
    }
    
    @Override
    protected void _writeChar(int index, char c)
    {
        changeToWriteState();
        memory.putChar(index, c);
    }
    
    @Override
    protected void _writeBoolean(int index, boolean b)
    {
        changeToWriteState();
        if (b)
        {
            memory.put(index, (byte) 0b01);
        }
        else
        {
            memory.put(index, (byte) 0b00);
        }
    }
    
    @Override
    public int readInt()
    {
        changeToReadState();
        int result = memory.getInt();
        readIndex += 4;
        return result;
    }
    
    @Override
    public short readShort()
    {
        changeToReadState();
        short s = memory.getShort();
        readIndex += 2;
        return s;
    }
    
    @Override
    public long readLong()
    {
        changeToReadState();
        long l = memory.getLong();
        readIndex += 8;
        return l;
    }
    
    @Override
    public char readChar()
    {
        changeToReadState();
        char c = memory.getChar();
        readIndex += 2;
        return c;
    }
    
    @Override
    public float readFloat()
    {
        changeToReadState();
        float f = memory.getFloat();
        readIndex += 4;
        return f;
    }
    
    @Override
    public double readDouble()
    {
        changeToReadState();
        double d = memory.getDouble();
        readIndex += 8;
        return d;
    }
    
    @Override
    public boolean readBoolean()
    {
        changeToReadState();
        if (memory.get() == 0)
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
        changeToReadState();
        return memory.getInt(index);
    }
    
    @Override
    public short readShort(int index)
    {
        changeToReadState();
        return memory.getShort(index);
    }
    
    @Override
    public long readLong(int index)
    {
        changeToReadState();
        return memory.getLong(index);
    }
    
    @Override
    public char readChar(int index)
    {
        changeToReadState();
        return memory.getChar(index);
    }
    
    @Override
    public float readFloat(int index)
    {
        changeToReadState();
        return memory.getFloat(index);
    }
    
    @Override
    public double readDouble(int index)
    {
        changeToReadState();
        return memory.getDouble(index);
    }
    
    @Override
    public String hexString()
    {
        changeToReadState();
        StringCache cache = new StringCache(remainRead());
        for (int i = readIndex; i < writeIndex; i++)
        {
            cache.append(DIGITS_LOWER[(memory.get(i) & 0xf0) >>> 4]);
            cache.append(DIGITS_LOWER[memory.get(i) & 0x0f]);
        }
        return cache.toString();
    }
    
    @Override
    public void writeLength(int length)
    {
        changeToWriteState();
        int newWriteIndex = 0;
        if (length <= 251)
        {
            newWriteIndex = writeIndex + 1;
            ensureCapacity(newWriteIndex);
            memory.put(writeIndex, (byte) length);
        }
        else if (length <= 255)
        {
            newWriteIndex = writeIndex + 2;
            ensureCapacity(newWriteIndex);
            memory.put(writeIndex, (byte) 252);
            memory.put(writeIndex + 1, (byte) length);
        }
        else if (length <= 0xffff)
        {
            newWriteIndex = writeIndex + 3;
            ensureCapacity(newWriteIndex);
            memory.put(writeIndex, (byte) 253);
            memory.put(writeIndex + 1, (byte) (length >> 8));
            memory.put(writeIndex + 2, (byte) length);
        }
        else if (length <= 0xffffff)
        {
            newWriteIndex = writeIndex + 4;
            ensureCapacity(newWriteIndex);
            memory.put(writeIndex, (byte) 254);
            memory.put(writeIndex + 1, (byte) (length >> 16));
            memory.put(writeIndex + 2, (byte) (length >> 8));
            memory.put(writeIndex + 3, (byte) length);
        }
        else
        {
            newWriteIndex = writeIndex + 5;
            ensureCapacity(newWriteIndex);
            memory.put(writeIndex, (byte) 255);
            memory.put(writeIndex + 1, (byte) (length >> 24));
            memory.put(writeIndex + 2, (byte) (length >> 16));
            memory.put(writeIndex + 3, (byte) (length >> 8));
            memory.put(writeIndex + 4, (byte) length);
        }
        writeIndex = newWriteIndex;
    }
    
    @Override
    public int readLength()
    {
        changeToReadState();
        int length = memory.get(readIndex++) & 0xff;
        if (length <= 251)
        {
            return length;
        }
        else if (length == 252)
        {
            length = memory.get(readIndex++) & 0xff;
            return length;
        }
        else if (length == 253)
        {
            length = (memory.get(readIndex++) & 0xff) << 8;
            length |= memory.get(readIndex++) & 0xff;
            return length;
        }
        else if (length == 254)
        {
            length = (memory.get(readIndex++) & 0xff) << 16;
            length |= (memory.get(readIndex++) & 0xff) << 8;
            length |= memory.get(readIndex++) & 0xff;
            return length;
        }
        else if (length == 255)
        {
            length = (memory.get(readIndex++) & 0xff) << 24;
            length |= (memory.get(readIndex++) & 0xff) << 16;
            length |= (memory.get(readIndex++) & 0xff) << 8;
            length |= memory.get(readIndex++) & 0xff;
            return length;
        }
        else
        {
            throw new RuntimeException("wrong data");
        }
    }
    
    public static DirectByteBuf allocate(int size)
    {
        return DirectByteBufPool.getInstance().get(size);
    }
    
    public static DirectByteBuf allocateWithTrace(int size)
    {
        DirectByteBuf buf = DirectByteBufPool.getInstance().get(size);
        buf.setTraceFlag(true);
        return buf;
    }
}
