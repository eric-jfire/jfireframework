package com.jfireframework.licp.serializer.extra;

import java.util.Calendar;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class CalendarSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        long time = ((Calendar) src).getTimeInMillis();
        buf.writeVarLong(time);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        long time = buf.readVarLong();
        Calendar calendar = Calendar.getInstance();
        licp.putObject(calendar);
        calendar.setTimeInMillis(time);
        return calendar;
    }
    
}
