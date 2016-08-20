package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;

public class TotalLengthFieldBasedFrameDecoderByHeap implements FrameDecodec
{
    // 代表长度字段开始读取的位置
    private final int          lengthFieldOffset;
    // 代表长度字段自身的长度。支持1,2,4.如果是1则使用unsignedbyte方式读取。如果是2则使用unsignedshort方式读取,4使用int方式读取。
    private final int          lengthFieldLength;
    // 将长度字段读取完毕，需要的偏移量,就是上面两个值相加
    private final int          lengthFieldEndOffset;
    // 需要忽略的字节数
    private final int          skipBytes;
    private final int          maxLegnth;
    private final DecodeResult result = new DecodeResult();
    
    /**
     * 
     * @param lengthFieldOffset 长度字段在报文中的偏移量
     * @param lengthFieldLength 长度字段本身的长度
     * @param skipBytes 解析后的报文需要跳过的位数
     * @param maxLength
     */
    public TotalLengthFieldBasedFrameDecoderByHeap(int lengthFieldOffset, int lengthFieldLength, int skipBytes, int maxLength)
    {
        this.lengthFieldLength = lengthFieldLength;
        this.lengthFieldOffset = lengthFieldOffset;
        this.maxLegnth = maxLength;
        lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
        this.skipBytes = skipBytes;
    }
    
    @Override
    public DecodeResult decodec(ByteBuf<?> ioBuffer)
    {
        ioBuffer.maskRead();
        if (lengthFieldEndOffset > ioBuffer.remainRead())
        {
            result.setType(DecodeResultType.BUF_NOT_ENOUGH);
            result.setNeed(lengthFieldEndOffset);
            return result;
        }
        // iobuffer中可能包含好几个报文，所以这里应该是增加的方式而不是直接设置的方式
        ioBuffer.addReadIndex(lengthFieldOffset);
        // 获取到整体报文的长度
        int length = 0;
        switch (lengthFieldLength)
        {
            case 1:
                length = ioBuffer.get() & 0xff;
                break;
            case 2:
                length = ioBuffer.readShort() & 0xff;
                break;
            case 4:
                length = ioBuffer.readInt();
                break;
        }
        // 得到整体长度后，开始从头读取这个长度的内容
        ioBuffer.resetRead();
        if (length >= maxLegnth)
        {
            result.setType(DecodeResultType.NOT_FIT_PROTOCOL);
            return result;
        }
        if (length > ioBuffer.remainRead())
        {
            result.setType(DecodeResultType.BUF_NOT_ENOUGH);
            result.setNeed(length);
            return result;
        }
        else
        {
            ByteBuf<?> buf = HeapByteBuf.allocate(length);
            buf.put(ioBuffer, length);
            ioBuffer.addReadIndex(length);
            if (skipBytes != 0)
            {
                buf.addReadIndex(skipBytes);
            }
            result.setType(DecodeResultType.NORMAL);
            result.setBuf(buf);
            return result;
        }
    }
}
