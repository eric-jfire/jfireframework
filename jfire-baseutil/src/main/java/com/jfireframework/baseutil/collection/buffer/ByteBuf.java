package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Queue;
import com.jfireframework.baseutil.code.CodeLocation;
import com.jfireframework.baseutil.collection.ByteCache;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.verify.Verify;

public abstract class ByteBuf<T>
{
    
    protected static int[]        offsets      = new int[] { 0, 8, 16, 24, 32, 40, 48, 56 };
    protected int                 capacity;
    protected int                 writeIndex   = 0;
    protected int                 maskWrite    = 0;
    protected int                 maskRead     = 0;
    protected int                 readIndex    = 0;
    protected T                   memory;
    protected Queue<T>            memHost;
    protected Queue<ByteBuf<T>>   bufHost;
    protected static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected String              releaseInfo;
    protected boolean             traceFlag    = false;
    
    public ByteBuf<?> maskRead()
    {
        maskRead = readIndex;
        return this;
    }
    
    public ByteBuf<?> resetRead()
    {
        readIndex = maskRead;
        return this;
    }
    
    public int readIndex()
    {
        return readIndex;
    }
    
    public ByteBuf<T> readIndex(int readIndex)
    {
        this.readIndex = readIndex;
        return this;
    }
    
    public ByteBuf<T> addReadIndex(int length)
    {
        readIndex += length;
        return this;
    }
    
    public ByteBuf<T> addWriteIndex(int length)
    {
        writeIndex += length;
        return this;
    }
    
    public int writeIndex()
    {
        return writeIndex;
    }
    
    public ByteBuf<?> maskWrite()
    {
        maskWrite = writeIndex;
        return this;
    }
    
    public ByteBuf<?> resetWrite()
    {
        writeIndex = maskWrite;
        return this;
    }
    
    public ByteBuf<T> writeIndex(int writeIndex)
    {
        this.writeIndex = writeIndex;
        return this;
    }
    
    public void releaseMemOnly()
    {
        readIndex = writeIndex = capacity = 0;
        if (memHost == null)
        {
            _release();
            return;
        }
        memHost.offer(memory);
        memory = null;
    }
    
    public void release()
    {
        releaseMemOnly();
        if (traceFlag)
        {
            releaseInfo = CodeLocation.getCodeLocation(4);
        }
        bufHost.offer(this);
    }
    
    protected abstract void _release();
    
    /**
     * 返回该ByteBuf的ByteBuffer视图。该视图的position为readIndex，limit为writeIndex。
     * 视图当前处于可读状态
     * 注意:
     * 该视图与ByteBuf共享存储。
     * 双方操作的修改是互相可见的。
     * 但是ByteBuffer中的position和limit与ByteBuf中的readIndex和writeIndex互相不干扰。
     * 
     * @return
     */
    public abstract ByteBuffer nioBuffer();
    
    /**
     * 将buffer的内容放入数组中,该操作会改变buffer的position位置
     * 
     * @param buffer
     */
    public void put(ByteBuffer buffer, int length)
    {
        int newCount = writeIndex + length;
        ensureCapacity(newCount);
        _put(buffer, length);
        writeIndex = newCount;
    }
    
    /**
     * 具体子类实现的写入
     * 
     * @param buffer
     * @param writeIndex
     * @param length
     */
    protected abstract void _put(ByteBuffer buffer, int length);
    
    /**
     * 将一个byte放入缓存类中
     * 
     * @param b
     */
    public ByteBuf<?> put(byte b)
    {
        int newCount = writeIndex + 1;
        ensureCapacity(newCount);
        _put(writeIndex, b);
        writeIndex = newCount;
        return this;
    }
    
    protected abstract void _put(int offset, byte b);
    
    public void put(int index, byte b)
    {
        _put(index, b);
    }
    
    /**
     * 将一个byte数组加入到缓存内容中
     * 
     * @param content
     * @return
     */
    public ByteBuf<T> put(byte[] content)
    {
        return put(content, 0, content.length);
    }
    
    public ByteBuf<T> put(byte[] content, int off, int len)
    {
        int newCount = writeIndex + len;
        ensureCapacity(newCount);
        _put(content, off, len);
        writeIndex = newCount;
        return this;
    }
    
    protected abstract void _put(byte[] content, int off, int len);
    
    /**
     * 确定缓存的剩余容量是否能满足参数需求的大小。如果不能，自动扩容到当前容量+参数容量的两倍
     * 
     * @param sizeneed
     * @return
     */
    public ByteBuf<T> ensureCapacity(int newSize)
    {
        if (capacity < newSize)
        {
            _expend(newSize);
        }
        return this;
    }
    
    /**
     * 将byteBuf扩容到指定的大小
     * 
     * @param capacity
     */
    protected abstract void _expend(int size);
    
    /**
     * 清除cache，将count和start同时设置为0
     * 
     * @return
     */
    public ByteBuf<T> clear()
    {
        writeIndex = 0;
        readIndex = 0;
        return this;
    }
    
    /**
     * 获取index位置的值。此操作不影响缓存内部状态
     * 
     * @param index
     * @return
     */
    public abstract byte get(int index);
    
    /**
     * 获取接下里的一个值，此操作将缓存的readIndex加1
     * 
     * @return
     */
    public abstract byte get();
    
    /**
     * 返回当前的缓存总量
     * 
     * @return
     */
    public int size()
    {
        return capacity;
    }
    
    /**
     * 获取剩余的可读字节数
     * 
     * @return
     */
    public int remainRead()
    {
        return writeIndex - readIndex;
    }
    
    /**
     * 获取剩余的可写字节数
     * 
     * @return
     */
    public int remainWrite()
    {
        return capacity - writeIndex;
    }
    
    /**
     * 将缓存内剩余的字节构造成一个数组并且返回。但是不影响缓存内的参数
     * 
     * @return
     */
    public abstract byte[] toArray();
    
    /**
     * 将readIndex和writeIndex之间的部分移动到数组最开始的地方，此时readIndex为0，writeIndex是writeIndex
     * -readIndex的值
     */
    public abstract ByteBuf<T> compact();
    
    /**
     * 从bytebuf中读取length长度的内容到content中
     * 
     * @param content 接受数据的byte数组
     * @param length 需要接受的长度
     */
    public void get(byte[] content, int length)
    {
        Verify.True(length <= remainRead(), "需要读取的长度太长，没有足够的数据可以读取");
        _get(content, 0, length);
    }
    
    public void get(byte[] content, int off, int len)
    {
        Verify.True(len <= remainRead(), "需要读取的长度太长，没有足够的数据可以读取");
        _get(content, off, len);
    }
    
    /**
     * 从bytebuf中读取length长度的内容到byte数组中
     * 
     * @param content
     * @param length
     */
    protected abstract void _get(byte[] content, int off, int length);
    
    /**
     * 从当前位置开始，读取长度为length的字节，以charset编码转化为string并返回。
     * cache的start位置前进length长度
     * 
     * @param charset
     * @param length
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public abstract String toString(Charset charset, int length);
    
    /**
     * 从当前位置开始，读取剩下的所有字节，按照charset组装成string并返回
     * 此时cache中无可读字节
     * 
     * @param charset
     * @return
     */
    public String toString(Charset charset)
    {
        return toString(charset, remainRead());
    }
    
    /**
     * 返回一个字符串描述当前的cache状态
     */
    public String toString()
    {
        return new StringCache("readIndex:").append(readIndex).appendComma().append("writeIndex:").append(writeIndex).appendComma().append("capacity:").append(capacity).toString();
    }
    
    /**
     * 将一个bytebuf的内容放入自身中,该操作不影响入参ByteBuf的内部参数
     * 
     * @param byteBuf
     * @return
     */
    public ByteBuf<T> put(ByteBuf<?> byteBuf)
    {
        return put(byteBuf, byteBuf.remainRead());
    }
    
    /**
     * 将一个bytebuf的length长度的内容放入到本bytebuf中。该操作不影响入参bytebuf的readIndex和writeIndex
     * 
     * @param byteBuf
     * @param length
     * @return
     */
    public ByteBuf<T> put(ByteBuf<?> byteBuf, int length)
    {
        int newWriteIndex = writeIndex + length;
        ensureCapacity(newWriteIndex);
        _put(byteBuf, length);
        writeIndex = newWriteIndex;
        return this;
    }
    
    /**
     * 将一个bytebuf的length长度的内容放入到本bytebuf中。该操作不影响入参bytebuf的readIndex和writeIndex
     * 
     * @param byteBuf
     * @param length
     */
    protected abstract void _put(ByteBuf<?> byteBuf, int length);
    
    /**
     * 写入一个int数据
     * 
     * @param i
     * @return
     */
    public ByteBuf<?> writeInt(int i)
    {
        int newWriteIndex = writeIndex + 4;
        ensureCapacity(newWriteIndex);
        _writeInt(writeIndex, i);
        writeIndex = newWriteIndex;
        return this;
    }
    
    public ByteBuf<?> writeInt(int index, int i)
    {
        _writeInt(index, i);
        return this;
    }
    
    protected abstract void _writeInt(int index, int i);
    
    public ByteBuf<?> writeShort(short s)
    {
        int newWriteIndex = writeIndex + 2;
        ensureCapacity(newWriteIndex);
        _writeShort(writeIndex, s);
        writeIndex = newWriteIndex;
        return this;
    }
    
    public ByteBuf<?> writeShort(int index, short s)
    {
        _writeShort(index, s);
        return this;
    }
    
    protected abstract void _writeShort(int index, short s);
    
    public ByteBuf<?> writeLong(long l)
    {
        int newWriteIndex = writeIndex + 8;
        ensureCapacity(newWriteIndex);
        _writeLong(writeIndex, l);
        writeIndex = newWriteIndex;
        return this;
    }
    
    public ByteBuf<?> writeMutableLengthLong(long num)
    {
        ensureCapacity(writeIndex + 9);
        if (num >= -112 && num <= 127)
        {
            put((byte) num);
            return this;
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
        put(first);
        for (int j = 0; j < length; j++)
        {
            put((byte) num);
            num = num >>> 8;
        }
        return this;
    }
    
    public long readMutableLengthLong()
    {
        byte head = get();
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
                out |= (get() & 0xffL) << offsets[i];
            }
            return out;
        }
    }
    
    public ByteBuf<?> writeLong(int index, long l)
    {
        _writeLong(index, l);
        return this;
    }
    
    protected abstract void _writeLong(int index, long l);
    
    public ByteBuf<?> writeFloat(float f)
    {
        return writeInt(Float.floatToRawIntBits(f));
    }
    
    public ByteBuf<?> writeFloat(int index, float f)
    {
        return writeInt(index, Float.floatToRawIntBits(f));
    }
    
    public ByteBuf<?> writeDouble(double d)
    {
        return writeLong(Double.doubleToRawLongBits(d));
    }
    
    public ByteBuf<?> writeDouble(int index, double d)
    {
        return writeLong(index, Double.doubleToRawLongBits(d));
    }
    
    public ByteBuf<?> writeChar(char c)
    {
        int newWriteIndex = writeIndex + 2;
        ensureCapacity(newWriteIndex);
        _writeChar(writeIndex, c);
        writeIndex = newWriteIndex;
        return this;
    }
    
    public ByteBuf<?> writeChar(int index, char c)
    {
        _writeChar(index, c);
        return this;
    }
    
    protected abstract void _writeChar(int index, char c);
    
    public ByteBuf<?> writeBoolean(boolean b)
    {
        int newWriteIndex = writeIndex + 1;
        ensureCapacity(newWriteIndex);
        _writeBoolean(writeIndex, b);
        writeIndex = newWriteIndex;
        return this;
    }
    
    protected abstract void _writeBoolean(int index, boolean b);
    
    public abstract int readInt();
    
    public abstract int readInt(int index);
    
    public abstract short readShort();
    
    public abstract short readShort(int index);
    
    public abstract long readLong();
    
    public abstract long readLong(int index);
    
    public abstract char readChar();
    
    public abstract char readChar(int index);
    
    public abstract float readFloat();
    
    public abstract float readFloat(int index);
    
    public abstract double readDouble();
    
    public abstract double readDouble(int index);
    
    public abstract boolean readBoolean();
    
    public void writeString(String str)
    {
        if (str == null)
        {
            writeInt(-1);
        }
        else
        {
            int length = str.length();
            ensureCapacity(writeIndex + 4 + (length << 1));
            _writeInt(writeIndex, length);
            writeIndex += 4;
            // writeLength(length);
            // ensureCapacity(writeIndex + length * 2);
            for (int i = 0; i < length; i++)
            {
                _writeChar(writeIndex + (i << 1), str.charAt(i));
            }
            writeIndex += length << 1;
        }
    }
    
    public String readString()
    {
        int length = readInt();
        if (length == -1)
        {
            return null;
        }
        char[] tmp = readCharArray(length);
        return new String(tmp);
    }
    
    /**
     * 匹配对src在bytebuf中的位置。如果匹配，就返回最开始的位置，如果不匹配，返回-1
     * 
     * @param src
     * @return
     */
    public int indexOf(byte[] src)
    {
        int length = src.length;
        next: for (int i = readIndex; i < writeIndex; i++)
        {
            if (get(i) == src[0] && i + length < writeIndex)
            {
                for (int j = 0; j < length; j++)
                {
                    if (get(i + j) == src[j])
                    {
                        
                    }
                    else
                    {
                        continue next;
                    }
                }
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 返回buf当前的十六进制内容。该操作不会影响读写指针位置
     * 
     * @return
     */
    public abstract String hexString();
    
    /**
     * 将ByteCache的数据全部放入ByteBuf中
     * 
     * @param byteCache
     * @return
     */
    public ByteBuf<?> putByteCache(ByteCache byteCache)
    {
        put(byteCache.getDirectArray(), byteCache.getReadindex(), byteCache.getWriteIndex());
        return this;
    }
    
    /**
     * 将数据读取到ByteCache中
     * 
     * @param byteCache
     * @return
     */
    public ByteBuf<?> get(ByteCache byteCache)
    {
        byteCache.putByteBuffer(nioBuffer());
        return this;
    }
    
    public ByteBuf<?> writeIntArray(int[] array)
    {
        writeInt(array.length);
        for (int i : array)
        {
            writeInt(i);
        }
        return this;
    }
    
    public int[] readIntArray()
    {
        int length = readInt();
        int[] tmp = new int[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readInt();
        }
        return tmp;
    }
    
    public int[] readIntArray(int length)
    {
        int[] tmp = new int[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readInt();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeLongArray(long[] array)
    {
        writeInt(array.length);
        for (long i : array)
        {
            writeLong(i);
        }
        return this;
    }
    
    public long[] readLongArray()
    {
        int length = readInt();
        long[] tmp = new long[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readLong();
        }
        return tmp;
    }
    
    public long[] readLongArray(int length)
    {
        long[] tmp = new long[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readLong();
        }
        return tmp;
    }
    
    public long[] readMutableLengthLongArray(int length)
    {
        long[] tmp = new long[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readMutableLengthLong();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeFloatArray(float[] array)
    {
        writeInt(array.length);
        for (float each : array)
        {
            writeFloat(each);
        }
        return this;
    }
    
    public float[] readFloatArray()
    {
        int length = readInt();
        return readFloatArray(length);
    }
    
    public float[] readFloatArray(int length)
    {
        float[] tmp = new float[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readFloat();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeDoubleArray(double[] array)
    {
        writeInt(array.length);
        for (double each : array)
        {
            writeDouble(each);
        }
        return this;
    }
    
    public double[] readDoubleArray()
    {
        int length = readInt();
        return readDoubleArray(length);
    }
    
    public double[] readDoubleArray(int length)
    {
        double[] tmp = new double[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readDouble();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeShortArray(short[] array)
    {
        writeInt(array.length);
        for (short each : array)
        {
            writeShort(each);
        }
        return this;
    }
    
    public short[] readShortArray()
    {
        int length = readInt();
        return readShortArray(length);
    }
    
    public short[] readShortArray(int length)
    {
        short[] tmp = new short[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readShort();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeCharArray(char[] array)
    {
        writeInt(array.length);
        ensureCapacity(array.length * 2 + writeIndex);
        for (char each : array)
        {
            _writeChar(writeIndex, each);
            writeIndex += 2;
        }
        return this;
    }
    
    public char[] readCharArray()
    {
        int length = readInt();
        return readCharArray(length);
    }
    
    public char[] readCharArray(int length)
    {
        char[] tmp = new char[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readChar();
        }
        return tmp;
    }
    
    public ByteBuf<?> writeByteArray(byte[] array)
    {
        writeInt(array.length);
        put(array);
        return this;
    }
    
    public byte[] readByteArray()
    {
        int length = readInt();
        return readByteArray(length);
    }
    
    public byte[] readByteArray(int length)
    {
        byte[] tmp = new byte[length];
        get(tmp, length);
        return tmp;
    }
    
    public ByteBuf<?> writeBooleanArray(boolean[] array)
    {
        writeInt(array.length);
        for (boolean each : array)
        {
            writeBoolean(each);
        }
        return this;
    }
    
    public boolean[] readBooleanArray()
    {
        int length = readInt();
        return readBooleanArray(length);
    }
    
    public boolean[] readBooleanArray(int length)
    {
        boolean[] tmp = new boolean[length];
        for (int i = 0; i < length; i++)
        {
            tmp[i] = readBoolean();
        }
        return tmp;
    }
    
    /**
     * 写入长度信息，由于长度信息大于等于0，所以可以有特殊的优化写法
     * 
     * @param length
     */
    public abstract void writeLength(int length);
    
    public abstract int readLength();
    
    public boolean isTraceFlag()
    {
        return traceFlag;
    }
    
    public void setTraceFlag(boolean traceFlag)
    {
        this.traceFlag = traceFlag;
    }
    
}
