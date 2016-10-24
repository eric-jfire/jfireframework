package com.jfireframework.licp.serializer.extra;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class LinkedListSerializer implements LicpSerializer
{
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        LinkedList<?> list = (LinkedList<?>) src;
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
        LinkedList<Object> list = new LinkedList<Object>();
        licp.putObject(list);
        int length = buf.readPositive();
        for (int i = 0; i < length; i++)
        {
            list.add(licp._deserialize(buf));
        }
        return list;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        LinkedList<Object> list = new LinkedList<Object>();
        licp.putObject(list);
        int length = BufferUtil.readPositive(buf);
        for (int i = 0; i < length; i++)
        {
            list.add(licp._deserialize(buf));
        }
        return list;
    }
    
}
