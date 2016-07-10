package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class LongSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeVarLong((Long) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Long l = buf.readVarLong();
        licp.putObject(l);
        return l;
    }
    
}
