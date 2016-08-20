package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;

public class LineBasedFrameDecodec implements FrameDecodec
{
    private int                maxLineLength;
    private final DecodeResult result = new DecodeResult();
    
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
    public DecodeResult decodec(ByteBuf<?> ioBuffer)
    {
        int eol = getEndOfLine(ioBuffer);
        if (eol == -1)
        {
            if (ioBuffer.remainRead() > maxLineLength)
            {
                result.setType(DecodeResultType.NOT_FIT_PROTOCOL);
                return result;
            }
            else
            {
                result.setType(DecodeResultType.BUF_NOT_ENOUGH);
                result.setNeed(1000);
                return result;
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
            result.setType(DecodeResultType.NORMAL);
            result.setBuf(frame);
            return result;
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
