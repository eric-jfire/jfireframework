package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class IntegerSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeInt((Integer) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Integer i = buf.readInt();
        licp.putObject(i);
        return i;
    }
    
}
