package com.jfireframework.licp.field;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public interface CacheField
{
    public String getName();
    
    public void write(Object holder, ByteBuf<?> buf, Licp licp);
    
    public void read(Object holder, ByteBuf<?> buf, Licp licp);
    
    public void read(Object holder, ByteBuffer buf, Licp licp);
    
}
