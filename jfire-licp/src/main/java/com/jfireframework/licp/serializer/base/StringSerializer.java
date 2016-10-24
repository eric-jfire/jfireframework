package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class StringSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        String value = (String) src;
        buf.writeString(value);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        String value = buf.readString();
        licp.putObject(value);
        return value;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        String value = BufferUtil.readString(buf);
        licp.putObject(value);
        return value;
    }
    
}
