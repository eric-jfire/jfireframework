package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class FloatSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeFloat((Float) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Float f = buf.readFloat();
        licp.putObject(f);
        return f;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        Float f = BufferUtil.readFloat(buf);
        licp.putObject(f);
        return f;
    }
    
}
