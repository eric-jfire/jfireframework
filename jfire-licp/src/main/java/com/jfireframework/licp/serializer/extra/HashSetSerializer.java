package com.jfireframework.licp.serializer.extra;

import java.nio.ByteBuffer;
import java.util.HashSet;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.util.BufferUtil;

public class HashSetSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        HashSet<?> set = (HashSet<?>) src;
        int length = set.size();
        buf.writePositive(length);
        for (Object each : set)
        {
            licp._serialize(each, buf);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        HashSet<Object> set = new HashSet<Object>(length);
        for (int i = 0; i < length; i++)
        {
            set.add(licp._deserialize(buf));
        }
        return set;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        HashSet<Object> set = new HashSet<Object>(length);
        for (int i = 0; i < length; i++)
        {
            set.add(licp._deserialize(buf));
        }
        return set;
    }
    
}
