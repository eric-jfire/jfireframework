package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

public class DirectByteBuf extends ByteBuf<ByteBuffer>
{
    
    private static long   offset = ReflectUtil.getFieldOffset("cleaner", ByteBuffer.allocateDirect(0).getClass());
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
    
    public DirectByteBuf(ByteBuffer memory, Queue<ByteBuffer> host, Queue<ByteBuf<ByteBuffer>> bufHost)
    {
        init(memory, host, bufHost);
    }
    
    public void init(ByteBuffer memory, Queue<ByteBuffer> host, Queue<ByteBuf<ByteBuffer>> bufHost)
    {
        this.memory = memory;
        this.bufHost = bufHost;
        this.memHost = host;
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
        cachedNioBuffer = null;
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
            memory.put(index, (byte) 0x01);
        }
        else
        {
            memory.put(index, (byte) 0x00);
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
    
    public void writePositive(int positive)
    {
        if (positive < 0)
        {
            throw new UnsupportedOperationException();
        }
        changeToWriteState();
        if (positive <= 251)
        {
            ensureCapacity(writeIndex + 1);
            memory.put(writeIndex, (byte) positive);
            writeIndex += 1;
        }
        else if (positive <= 255)
        {
            ensureCapacity(writeIndex + 2);
            memory.put(writeIndex, (byte) 252);
            memory.put(writeIndex + 1, (byte) positive);
            writeIndex += 2;
        }
        else if (positive <= 0xffff)
        {
            ensureCapacity(writeIndex + 3);
            memory.put(writeIndex, (byte) 253);
            memory.put(writeIndex + 1, (byte) (positive >> 8));
            memory.put(writeIndex + 2, (byte) positive);
            writeIndex += 3;
        }
        else if (positive <= 0xffffff)
        {
            ensureCapacity(writeIndex + 4);
            memory.put(writeIndex, (byte) 254);
            memory.put(writeIndex + 1, (byte) (positive >> 16));
            memory.put(writeIndex + 2, (byte) (positive >> 8));
            memory.put(writeIndex + 3, (byte) positive);
            writeIndex += 4;
        }
        else
        {
            ensureCapacity(writeIndex + 5);
            memory.put(writeIndex, (byte) 255);
            memory.put(writeIndex + 1, (byte) (positive >> 24));
            memory.put(writeIndex + 2, (byte) (positive >> 16));
            memory.put(writeIndex + 3, (byte) (positive >> 8));
            memory.put(writeIndex + 4, (byte) positive);
            writeIndex += 5;
        }
    }
    
    public int readPositive()
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
    
    @Override
    public DirectByteBuf writeVarint(int i)
    {
        if (i >= -120 && i <= 127)
        {
            ensureCapacity(writeIndex + 1);
            changeToWriteState();
            memory.put((byte) i);
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
            changeToWriteState();
            memory.put((byte) (head - 1)).put((byte) i);
            writeIndex += 2;
        }
        else if (i <= 0x0000ffff)
        {
            ensureCapacity(writeIndex + 3);
            changeToWriteState();
            memory.put((byte) (head - 2)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 3;
        }
        else if (i <= 0x00ffffff)
        {
            ensureCapacity(writeIndex + 4);
            changeToWriteState();
            memory.put((byte) (head - 3)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 4;
        }
        else
        {
            ensureCapacity(writeIndex + 5);
            changeToWriteState();
            memory.put((byte) (head - 4)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 5;
        }
        return this;
    }
    
    @Override
    public int readVarint()
    {
        changeToReadState();
        byte b = memory.get();
        if (b >= -120 && b <= 127)
        {
            readIndex += 1;
            return b;
        }
        switch (b)
        {
            case -121:
                readIndex += 2;
                return memory.get() & 0xff;
            case -122:
                readIndex += 3;
                return ((memory.get() & 0xff) << 8) | (memory.get() & 0xff);
            case -123:
                readIndex += 4;
                return ((memory.get() & 0xff) << 16) | ((memory.get() & 0xff) << 8) | (memory.get() & 0xff);
            case -124:
                readIndex += 5;
                return ((memory.get() & 0xff) << 24) | ((memory.get() & 0xff) << 16) | ((memory.get() & 0xff) << 8) | (memory.get() & 0xff);
            case -125:
                readIndex += 2;
                return ~(memory.get() & 0xff);
            case -126:
                readIndex += 3;
                return ~(((memory.get() & 0xff) << 8) | (memory.get() & 0xff));
            case -127:
                readIndex += 4;
                return ~(((memory.get() & 0xff) << 16) | ((memory.get() & 0xff) << 8) | (memory.get() & 0xff));
            case -128:
                readIndex += 5;
                return ~(((memory.get() & 0xff) << 24) | ((memory.get() & 0xff) << 16) | ((memory.get() & 0xff) << 8) | (memory.get() & 0xff));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    @Override
    public DirectByteBuf writeVarLong(long i)
    {
        if (i >= -112 && i <= 127)
        {
            ensureCapacity(writeIndex + 1);
            changeToWriteState();
            memory.put((byte) i);
            writeIndex += 1;
            return this;
        }
        int head = -112;
        if (i < 0)
        {
            i = ~i;
            head = -120;
        }
        if (i <= 0x000000ff)
        {
            ensureCapacity(writeIndex + 2);
            changeToWriteState();
            memory.put((byte) (head - 1)).put((byte) i);
            writeIndex += 2;
        }
        else if (i <= 0x0000ffff)
        {
            ensureCapacity(writeIndex + 3);
            changeToWriteState();
            memory.put((byte) (head - 2)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 3;
        }
        else if (i <= 0x00ffffff)
        {
            ensureCapacity(writeIndex + 4);
            changeToWriteState();
            memory.put((byte) (head - 3)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 4;
        }
        else if (i <= 0x00000000ffffffffl)
        {
            ensureCapacity(writeIndex + 5);
            changeToWriteState();
            memory.put((byte) (head - 4)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 5;
        }
        else if (i <= 0x000000ffffffffffl)
        {
            ensureCapacity(writeIndex + 6);
            changeToWriteState();
            memory.put((byte) (head - 5)).put((byte) (i >>> 32)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 6;
        }
        else if (i <= 0x0000ffffffffffffl)
        {
            ensureCapacity(writeIndex + 7);
            changeToWriteState();
            memory.put((byte) (head - 6)).put((byte) (i >>> 40)).put((byte) (i >>> 32)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 7;
        }
        else if (i <= 0x00ffffffffffffffl)
        {
            ensureCapacity(writeIndex + 8);
            changeToWriteState();
            memory.put((byte) (head - 7)).put((byte) (i >>> 48)).put((byte) (i >>> 40)).put((byte) (i >>> 32)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 8;
        }
        else
        {
            ensureCapacity(writeIndex + 9);
            changeToWriteState();
            memory.put((byte) (head - 8)).put((byte) (i >>> 56)).put((byte) (i >>> 48)).put((byte) (i >>> 40)).put((byte) (i >>> 32)).put((byte) (i >>> 24)).put((byte) (i >>> 16)).put((byte) (i >>> 8)).put((byte) i);
            writeIndex += 9;
        }
        return this;
    }
    
    @Override
    public long readVarLong()
    {
        changeToReadState();
        byte b = memory.get();
        if (b >= -112 && b <= 127)
        {
            readIndex += 1;
            return b;
        }
        switch (b)
        {
            case -113:
                readIndex += 2;
                return memory.get() & 0xffl;
            case -114:
                readIndex += 3;
                return ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -115:
                readIndex += 4;
                return ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -116:
                readIndex += 5;
                return ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -117:
                readIndex += 6;
                return ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -118:
                readIndex += 7;
                return ((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -119:
                readIndex += 8;
                return ((memory.get() & 0xffl) << 48) | ((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -120:
                readIndex += 9;
                return ((memory.get() & 0xffl) << 56) | ((memory.get() & 0xffl) << 48) | ((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl);
            case -121:
                readIndex += 2;
                return ~(memory.get() & 0xffl);
            case -122:
                readIndex += 3;
                return ~(((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -123:
                readIndex += 4;
                return ~(((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -124:
                readIndex += 5;
                return ~(((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -125:
                readIndex += 6;
                return ~(((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -126:
                readIndex += 7;
                return ~(((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -127:
                readIndex += 8;
                return ~(((memory.get() & 0xffl) << 48) | ((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            case -128:
                readIndex += 9;
                return ~(((memory.get() & 0xffl) << 56) | ((memory.get() & 0xffl) << 48) | ((memory.get() & 0xffl) << 40) | ((memory.get() & 0xffl) << 32) | ((memory.get() & 0xffl) << 24) | ((memory.get() & 0xffl) << 16) | ((memory.get() & 0xffl) << 8) | (memory.get() & 0xffl));
            default:
                throw new UnSupportException("not here");
        }
    }
    
    @Override
    public ByteBuf<ByteBuffer> writeVarChar(char c)
    {
        changeToWriteState();
        ensureCapacity(writeIndex + 3);
        _writeVarChar(c);
        return this;
    }
    
    private ByteBuf<ByteBuffer> _writeVarChar(char c)
    {
        int positive = c;
        if (positive <= 251)
        {
            memory.put(writeIndex, (byte) positive);
            writeIndex += 1;
        }
        else if (positive <= 255)
        {
            memory.put(writeIndex, (byte) 252);
            memory.put(writeIndex + 1, (byte) positive);
            writeIndex += 2;
        }
        else if (positive <= 0xffff)
        {
            memory.put(writeIndex, (byte) 253);
            memory.put(writeIndex + 1, (byte) (positive >>> 8));
            memory.put(writeIndex + 2, (byte) positive);
            writeIndex += 3;
        }
        return this;
    }
    
    @Override
    public char readVarChar()
    {
        changeToReadState();
        int length = memory.get(readIndex++) & 0xff;
        if (length <= 251)
        {
            return (char) length;
        }
        else if (length == 252)
        {
            length = memory.get(readIndex++) & 0xff;
            return (char) length;
        }
        else if (length == 253)
        {
            length = (memory.get(readIndex++) & 0xff) << 8;
            length |= memory.get(readIndex++) & 0xff;
            return (char) length;
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    @Override
    public ByteBuf<ByteBuffer> writeString(String value)
    {
        if (value == null)
        {
            throw new NullPointerException();
        }
        int length = value.length();
        writePositive(length);
        changeToWriteState();
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
