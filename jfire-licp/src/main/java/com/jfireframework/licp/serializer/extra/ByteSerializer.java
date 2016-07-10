package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class ByteSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.put((Byte) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Byte b = buf.get();
        licp.putObject(b);
        return b;
    }
    
}
