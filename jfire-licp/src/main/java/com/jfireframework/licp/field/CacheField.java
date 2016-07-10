package com.jfireframework.licp.field;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public interface CacheField
{
    public String getName();
    
    public void write(Object holder, ByteBuf<?> buf, Licp licp);
    
    public void read(Object holder, ByteBuf<?> buf, Licp licp);
    
}
