package com.jfireframework.licp.serializer.extra;

import java.util.Date;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class DateSerializer implements LicpSerializer
{
    private final boolean sqlDate;
    
    public DateSerializer(boolean sqlDate)
    {
        this.sqlDate = sqlDate;
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        if (sqlDate)
        {
            java.sql.Date date = (java.sql.Date) src;
            buf.writeLong(date.getTime());
        }
        else
        {
            Date date = (Date) src;
            buf.writeLong(date.getTime());
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        long time = buf.readLong();
        if (sqlDate)
        {
            Object result = new java.sql.Date(time);
            licp.putObject(result);
            return result;
        }
        else
        {
            Object result = new Date(time);
            licp.putObject(result);
            return result;
        }
    }
    
}
