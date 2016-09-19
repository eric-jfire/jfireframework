package com.jfireframework.baseutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import com.jfireframework.baseutil.collection.ByteCache;
import com.jfireframework.baseutil.exception.JustThrowException;

public class LineReader
{
    /** 当前使用的编码格式 */
    private Charset          charset;
    /** 进行文件随机读取的类 */
    private RandomAccessFile randomAccessFile;
    /** 缓存当前行内容的cache */
    private ByteCache        cache      = new ByteCache();
    /** 当前读取位置的偏移量，该偏移量不会停留在/r/n上，始终停留在可以读取到字符的位置 */
    private long             offset     = 0;
    /** 回车符 /r */
    private final static int CR         = 0X0D;
    /** 换行符 /n */
    private final static int LF         = 0x0A;
    private int              readLength = 1024;
    
    public LineReader(File file, Charset charset, int readLength)
    {
        this(file, charset);
        this.readLength = readLength;
    }
    
    /**
     * 使用一个文件和文件编码进行初始化
     * 
     * @param file
     * @param charset
     */
    public LineReader(File file, Charset charset)
    {
        this.charset = charset;
        try
        {
            randomAccessFile = new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    /**
     * 读取file的内容到缓存中，并且返回当前读取的行。
     * 
     * @return
     */
    public String readLine()
    {
        int start = cache.getReadindex();
        int readTotal = 0;
        boolean hasCr = false;
        boolean hasOneLine = false;
        String line = null;
        while (hasOneLine == false && readTotal != -1)
        {
            if (cache.remaining() == 0)
            {
                readTotal = cache.read(randomAccessFile, readLength);
                continue;
            }
            int count = cache.getWriteIndex();
            byte[] array = cache.getDirectArray();
            for (; start < count; start++)
            {
                if (array[start] == CR)
                {
                    hasCr = true;
                    continue;
                }
                if (array[start] == LF)
                {
                    hasOneLine = true;
                    break;
                }
            }
            if (hasOneLine)
            {
                if (hasCr)
                {
                    line = cache.toString(charset, start - 1 - cache.getReadindex());
                    cache.get();
                    cache.get();
                }
                else
                {
                    line = cache.toString(charset, start - cache.getReadindex());
                    cache.get();
                }
                return line;
            }
            else
            {
                start -= cache.getReadindex();
                cache.compact();
                readTotal = cache.read(randomAccessFile, readLength);
            }
        }
        if (readTotal == -1 && cache.remaining() > 0)
        {
            return cache.toString(charset);
        }
        else
        {
            return null;
        }
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void close()
    {
        try
        {
            randomAccessFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
