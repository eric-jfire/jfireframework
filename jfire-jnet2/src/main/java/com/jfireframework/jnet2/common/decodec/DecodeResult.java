package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class DecodeResult
{
    private DecodeResultType type;
    private int              need = -1;
    private ByteBuf<?>       buf;
    
    public DecodeResultType getType()
    {
        return type;
    }
    
    public void setType(DecodeResultType type)
    {
        this.type = type;
    }
    
    public int getNeed()
    {
        return need;
    }
    
    public void setNeed(int need)
    {
        this.need = need;
    }
    
    public ByteBuf<?> getBuf()
    {
        return buf;
    }
    
    public void setBuf(ByteBuf<?> buf)
    {
        this.buf = buf;
    }
    
}
