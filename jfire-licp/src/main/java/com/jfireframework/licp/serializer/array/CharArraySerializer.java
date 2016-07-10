package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class CharArraySerializer extends AbstractArraySerializer
{
    
    public CharArraySerializer()
    {
        super(char[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        char[] array = (char[]) src;
        buf.writePositive(array.length);
        for (char each : array)
        {
            buf.writeChar(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        char[] array = new char[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readChar();
        }
        return array;
    }
    
}
