package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.jnet2.common.exception.BufNotEnoughException;
import com.jfireframework.jnet2.common.exception.LessThanProtocolException;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;

public class LineBasedFrameDecodec implements FrameDecodec
{
    private int maxLineLength;
    
    /**
     * 换行符报文解码器。
     * 
     * @param maxLineLength 可读取的最大长度，超过最大长度还未读取到换行符，则抛出异常
     */
    public LineBasedFrameDecodec(int maxLineLength)
    {
        this.maxLineLength = maxLineLength;
    }
    
    @Override
    public ByteBuf<?> decodec(ByteBuf<?> ioBuffer) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException
    {
        int eol = getEndOfLine(ioBuffer);
        if (eol == -1)
        {
            if (ioBuffer.remainRead() > maxLineLength)
            {
                throw NotFitProtocolException.instance;
            }
            else
            {
                throw new BufNotEnoughException(1000);
            }
        }
        else
        {
            int length;
            if ('\r' == ioBuffer.get(eol - 1))
            {
                length = eol - ioBuffer.readIndex() - 1;
            }
            else
            {
                length = eol - ioBuffer.readIndex();
            }
            DirectByteBuf frame = DirectByteBufPool.getInstance().get(length);
            frame.put(ioBuffer, length);
            ioBuffer.readIndex(eol + 1);
            return frame;
        }
    }
    
    private int getEndOfLine(ByteBuf<?> byteBuf)
    {
        final int readIndex = byteBuf.readIndex();
        final int writeIndex = byteBuf.writeIndex();
        for (int i = readIndex; i < writeIndex; i++)
        {
            byte b;
            try
            {
                b = byteBuf.get(i);
            }
            catch (Exception e)
            {
                System.err.println(readIndex);
                System.err.println(writeIndex);
                System.err.println(i);
                throw e;
            }
            if (b == '\n')
            {
                return i;
            }
        }
        return -1;
    }
}
