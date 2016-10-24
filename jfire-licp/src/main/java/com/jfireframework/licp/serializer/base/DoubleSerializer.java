package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class DoubleSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeDouble((Double) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Double d = buf.readDouble();
        licp.putObject(d);
        return d;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        Double d = BufferUtil.readDouble(buf);
        licp.putObject(d);
        return d;
    }
    
}
