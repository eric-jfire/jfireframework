package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class BooleanArraySerializer extends AbstractArraySerializer
{
    
    public BooleanArraySerializer()
    {
        super(boolean[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        boolean[] array = (boolean[]) src;
        buf.writeInt(array.length);
        for (boolean each : array)
        {
            buf.writeBoolean(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        boolean[] array = new boolean[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readBoolean();
        }
        return array;
    }
    
}
