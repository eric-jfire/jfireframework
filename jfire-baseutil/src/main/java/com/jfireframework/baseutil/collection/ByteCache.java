package com.jfireframework.baseutil.collection;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 高级的byte数组包装类，提供自动增长的功能。
 * 内容有写入位置，读取位置，容量三个关键参数。
 * 在写入操作时会自动移动写入位置，在读取操作时会自动移动读取位置。
 * 为了提高性能，不在读取时检查是否读取位置超越写入位置。
 * 该类不是线程安全的，多线程环境下，容量自动增长会出现不可预知的问题
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class ByteCache
{
    private byte[] array;
    private int    size;
    private int    writeIndex = 0;
    private int    readIndex  = 0;
                              
    /**
     * 使用一个byte数组初始化类，类的初始容量为数组容量
     * 
     * @param array
     */
    public ByteCache(byte[] array)
    {
        this.array = array;
        size = array.length;
    }
    
    /**
     * 指定类的初始容量
     * 
     * @param size
     */
    public ByteCache(int size)
    {
        if (array == null)
        {
            array = new byte[size];
        }
        else
        {
            if (this.size < size)
            {
                array = new byte[size];
            }
        }
        writeIndex = 0;
        readIndex = 0;
        this.size = array.length;
    }
    
    /**
     * 使用bytebbuffer初始化该类，并且当前内容为bytebuffer的剩余内容
     * 
     * @param buffer
     */
    public ByteCache(ByteBuffer buffer)
    {
        this(buffer.remaining() * 2);
        putByteBuffer(buffer);
    }
    
    /**
     * 将buffer的内容放入数组中,该操作会改变buffer的position位置
     * 
     * @param buffer
     */
    public void putByteBuffer(ByteBuffer buffer)
    {
        int length = buffer.remaining();
        int newCount = writeIndex + length;
        ensureCapacity(newCount);
        buffer.get(array, writeIndex, length);
        writeIndex = newCount;
    }
    
    /**
     * 默认构造方法，指定初始大小为4096
     */
    public ByteCache()
    {
        this(128);
    }
    
    /**
     * 将一个byte放入缓存类中
     * 
     * @param b
     */
    public void put(byte b)
    {
        int newCount = writeIndex + 1;
        ensureCapacity(newCount);
        array[writeIndex] = b;
        writeIndex = newCount;
    }
    
    /**
     * 确认剩余的空间足够容纳需要的长度。否则长度扩充
     * 
     * @param need
     */
    public void ensureLeft(int need)
    {
        if (size - writeIndex < need)
        {
            int newSize = size << 1;
            if (newSize < size)
            {
                size = Integer.MAX_VALUE;
            }
            else
            {
                size = newSize;
            }
            byte[] tmp = new byte[size];
            System.arraycopy(array, 0, tmp, 0, writeIndex);
            array = tmp;
        }
    }
    
    /**
     * 将一个byte加入到缓存内容中
     * 该方法不检查缓存容量，请注意是否会带来溢出问题。
     * 由于不检查容量，性能会比检查容量的方法好
     * 
     * @param b
     * @return
     */
    public ByteCache putWithoutCheck(byte b)
    {
        array[writeIndex] = b;
        writeIndex++;
        return this;
    }
    
    /**
     * 将一个byte数组加入到缓存内容中
     * 
     * @param content
     * @return
     */
    public ByteCache putArray(byte[] content)
    {
        int length = content.length;
        int newCount = writeIndex + length;
        ensureCapacity(newCount);
        System.arraycopy(content, 0, array, writeIndex, length);
        writeIndex = newCount;
        return this;
    }
    
    public ByteCache putArray(byte[] content, int off, int len)
    {
        int newCount = writeIndex + len;
        ensureCapacity(newCount);
        System.arraycopy(content, off, array, writeIndex, len);
        writeIndex = newCount;
        return this;
    }
    
    /**
     * 从bytecache中读取length长度的内容到content中
     * 
     * @param content 接受数据的byte数组
     * @param length 需要接受的长度
     */
    public void getArray(byte[] content, int length)
    {
        if (length > remaining())
        {
            throw new RuntimeException("需要读取的长度太长，没有足够的数据可以读取");
        }
        else
        {
            
            System.arraycopy(array, readIndex, content, 0, length);
            readIndex += length;
        }
    }
    
    /**
     * 确定缓存的剩余容量是否能满足参数需求的大小。如果不能，自动扩容到当前容量+参数容量的两倍
     * 
     * @param sizeneed
     * @return
     */
    public ByteCache ensureCapacity(int newSize)
    {
        if (size < newSize)
        {
            size += newSize;
            if (size < newSize)
            {
                size = Integer.MAX_VALUE;
            }
            byte[] tmp = new byte[size];
            System.arraycopy(array, 0, tmp, 0, writeIndex);
            array = tmp;
        }
        return this;
    }
    
    /**
     * 清除cache，将count和start同时设置为0
     * 
     * @return
     */
    public ByteCache clear()
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
    public byte get(int index)
    {
        return array[index];
    }
    
    /**
     * 获取接下里的一个值，此操作将缓存的start加1
     * 
     * @return
     */
    public byte get()
    {
        return array[readIndex++];
    }
    
    /**
     * 设定缓存开始读取的位置
     * 
     * @param index
     * @return
     */
    public ByteCache startRead(int index)
    {
        readIndex = index;
        return this;
    }
    
    /**
     * 返回当前的写入位置
     * 
     * @return
     */
    public int getWriteIndex()
    {
        return writeIndex;
    }
    
    /**
     * 返回当前的读取位置
     * 
     * @return
     */
    public int getReadindex()
    {
        return readIndex;
    }
    
    /**
     * 返回当前的缓存总量
     * 
     * @return
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * 获取剩余的读取字节，采用count-start作为结果
     * 
     * @return
     */
    public int remaining()
    {
        return writeIndex - readIndex;
    }
    
    /**
     * 将缓存内剩余的字节构造成一个数组并且返回。但是不影响缓存内的参数
     * 
     * @return
     */
    public byte[] toArray()
    {
        byte[] result = new byte[remaining()];
        System.arraycopy(array, readIndex, result, 0, remaining());
        return result;
    }
    
    /**
     * 将另外一个bytecache的内容放入到自身当中,不会影响入参的bytecache的内容数据和内部参数
     * 
     * @param src
     */
    public void putByteCache(ByteCache src)
    {
        int length = src.remaining();
        int newCount = writeIndex + length;
        ensureCapacity(newCount);
        System.arraycopy(src.getDirectArray(), src.getReadindex(), array, writeIndex, length);
        writeIndex = newCount;
    }
    
    /**
     * 获取内部的直接数组
     * 
     * @return
     */
    public byte[] getDirectArray()
    {
        return array;
    }
    
    /**
     * 读取buffer的数据，读取length的长度，读取之后，不会改变buffer的position信息
     * 
     * @param buffer
     * @param length
     * @return
     */
    public ByteCache putByteBuffer(ByteBuffer buffer, int length)
    {
        int originalPosi = buffer.position();
        int newCount = writeIndex + length;
        ensureCapacity(length);
        buffer.get(array, writeIndex, length);
        buffer.position(originalPosi);
        writeIndex = newCount;
        return this;
    }
    
    /**
     * 将start和count之间的部分移动到数组最开始的地方，此时start为0，count是count-start的值
     */
    public void compact()
    {
        System.arraycopy(array, readIndex, array, 0, remaining());
        writeIndex -= readIndex;
        readIndex = 0;
    }
    
    /**
     * 从当前位置开始，读取长度为length的字节，以charset编码转化为string并返回。
     * cache的start位置前进length长度
     * 
     * @param charset
     * @param length
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public String toString(Charset charset, int length)
    {
        if (length > remaining())
        {
            throw new RuntimeException("需要读取的字节太多");
        }
        String result = new String(array, readIndex, length, charset);
        readIndex += length;
        return result;
    }
    
    /**
     * 从当前位置开始，读取剩下的所有字节，按照charset组装成string并返回
     * 此时cache中无可读字节
     * 
     * @param charset
     * @return
     */
    public String toString(Charset charset)
    {
        return toString(charset, remaining());
    }
    
    /**
     * 返回一个字符串描述当前的cache状态
     */
    public String toString()
    {
        return new StringCache("start:").append(readIndex).appendComma().append("count:").append(writeIndex).appendComma().append("capacity:").append(size).toString();
    }
    
    public void setCount(int count)
    {
        if (count < readIndex)
        {
            throw new RuntimeException("count不能比start小");
        }
        this.writeIndex = count;
    }
    
    public int read(RandomAccessFile randomAccessFile, int length)
    {
        int newCount = writeIndex + length;
        ensureCapacity(newCount);
        try
        {
            int result = randomAccessFile.read(array, writeIndex, length);
            if (result != -1)
            {
                writeIndex += result;
            }
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException("读取文件发生异常");
        }
        
    }
}
