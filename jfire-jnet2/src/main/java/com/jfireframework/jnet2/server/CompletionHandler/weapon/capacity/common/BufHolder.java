package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.common;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public final class BufHolder
{
    private volatile ByteBuf<?> buf;
    
    public ByteBuf<?> getBuf()
    {
        return buf;
    }
    
    public void setBuf(ByteBuf<?> buf)
    {
        this.buf = buf;
    }
}
