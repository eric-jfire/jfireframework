package com.jfireframework.licp.serializer.extra;

import java.util.HashMap;
import java.util.Map.Entry;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class HashMapSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        HashMap<?, ?> map = (HashMap<?, ?>) src;
        licp.putObject(map);
        int size = map.size();
        buf.writePositive(size);
        for (Entry<?, ?> entry : map.entrySet())
        {
            licp._serialize(entry.getKey(), buf);
            licp._serialize(entry.getValue(), buf);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int size = buf.readPositive();
        HashMap<Object, Object> map = new HashMap<Object, Object>(size);
        licp.putObject(map);
        for (int i = 0; i < size; i++)
        {
            Object key = licp._deserialize(buf);
            Object value = licp._deserialize(buf);
            map.put(key, value);
        }
        return map;
    }
    
}
