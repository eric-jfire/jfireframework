package com.jfireframework.licp.serializer.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class ArraylistSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        ArrayList<?> list = (ArrayList<?>) src;
        int length = list.size();
        buf.writePositive(length);
        for (Object each : list)
        {
            licp._serialize(each, buf);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        ArrayList<Object> list = new ArrayList<Object>(length);
        licp.putObject(list);
        for (int i = 0; i < length; i++)
        {
            list.add(licp._deserialize(buf));
        }
        return list;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        ArrayList<Object> list = new ArrayList<Object>(length);
        licp.putObject(list);
        for (int i = 0; i < length; i++)
        {
            list.add(licp._deserialize(buf));
        }
        return list;
    }
    
}
