package com.jfireframework.baseutil.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTool
{
    /**
     * 从输出流读取数据直到流的末尾
     * 
     * @param inputStream
     * @param outputStream
     * @param cacheSize
     * @throws IOException
     */
    public static void transfer(InputStream inputStream, OutputStream outputStream, int cacheSize) throws IOException
    {
        byte[] cache = new byte[cacheSize];
        int read = 0;
        while ((read = inputStream.read(cache)) != -1)
        {
            outputStream.write(cache, 0, read);
        }
    }
    
    /**
     * 将读取流中的数据传输到输出流。从offset开始，传输length长度的字节。每次读取的缓存大小是cacheSize
     * 
     * @param inputStream
     * @param outputStream
     * @param cacheSize
     * @param offset
     * @param length
     * @throws IOException
     */
    public static void transfer(InputStream inputStream, OutputStream outputStream, int cacheSize, int offset, int length) throws IOException
    {
        // 大小取两者中的最小值
        byte[] cache = new byte[cacheSize > length ? length : cacheSize];
        int read = 0;
        inputStream.skip(offset);
        while ((read = inputStream.read(cache)) != -1)
        {
            if (read >= length)
            {
                outputStream.write(cache, 0, length);
                break;
            }
            else
            {
                outputStream.write(cache, 0, read);
                length -= read;
            }
        }
    }
}
