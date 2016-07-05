package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class IntArraySerializer extends AbstractArraySerializer
{
    
    public IntArraySerializer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    protected void writeOneDim(Object array, ByteBuf<?> buf, Licp licp)
    {
        int[] ints = (int[]) array;
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
