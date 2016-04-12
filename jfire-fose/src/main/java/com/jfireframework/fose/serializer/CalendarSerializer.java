package com.jfireframework.fose.serializer;

import java.util.Calendar;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;

public class CalendarSerializer implements Serializer
{
	
	@Override
	public void getObjects(Object src, ObjectCollect collect)
	{
		collect.add(src);
	}
	
	@Override
	public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Calendar calendar = (Calendar) src;
		buf.writeMutableLengthLong(calendar.getTimeInMillis());
	}
	
	@Override
	public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Calendar calendar = (Calendar) target;
		calendar.setTimeInMillis(buf.readMutableLengthLong());
	}
	
}
