package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class CharSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        buf.writeChar((Character) src);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        Character c = buf.readChar();
        licp.putObject(c);
        return c;
    }
    
}
