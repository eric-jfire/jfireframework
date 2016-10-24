package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class IntegerSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeVarint((Integer) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Integer i = buf.readVarint();
        licp.putObject(i);
        return i;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        Integer i = BufferUtil.readVarint(buf);
        licp.putObject(i);
        return i;
    }
    
}
