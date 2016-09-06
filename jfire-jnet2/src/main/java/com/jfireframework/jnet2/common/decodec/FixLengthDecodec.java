package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;

public class FixLengthDecodec implements FrameDecodec
{
    private final int          frameLength;
    private final DecodeResult result = new DecodeResult();
    
    /**
     * 固定长度解码器
     * 
     * @param frameLength 一个报文的固定长度
     */
    public FixLengthDecodec(int frameLength)
    {
        this.frameLength = frameLength;
    }
    
    @Override
    public DecodeResult decodec(ByteBuf<?> ioBuf)
    {
        if (ioBuf.remainRead() < frameLength)
        {
            result.setType(DecodeResultType.BUF_NOT_ENOUGH);
            result.setNeed(frameLength);
            return result;
        }
        ByteBuf<?> buf = DirectByteBufPool.getInstance().get(frameLength);
        buf.put(ioBuf, frameLength);
        ioBuf.addReadIndex(frameLength);
        result.setType(DecodeResultType.NORMAL);
        result.setBuf(buf);
        return result;
    }
    
}
