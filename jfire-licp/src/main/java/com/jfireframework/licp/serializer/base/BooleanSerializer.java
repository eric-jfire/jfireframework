package com.jfireframework.licp.serializer.base;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class BooleanSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        if ((Boolean) src)
        {
            buf.put((byte) 0);
        }
        else
        {
            buf.put((byte) 1);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        if (buf.get() == 1)
        {
            licp.putObject(true);
            return true;
        }
        else
        {
            licp.putObject(false);
            return false;
        }
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        if (buf.get() == 1)
        {
            licp.putObject(true);
            return true;
        }
        else
        {
            licp.putObject(false);
            return false;
        }
    }
    
}
