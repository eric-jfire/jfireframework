package com.jfireframework.fose.serializer;

import java.util.Date;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;

public class DateSerializer implements Serializer
{
	@Override
	public void getObjects(Object src, ObjectCollect collect)
	{
		collect.add(src);
	}
	
	@Override
	public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Date tmpDate = (Date) src;
		buf.writeMutableLengthLong(tmpDate.getTime());
	}
	
	@Override
	public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		long time = buf.readMutableLengthLong();
		Date tmpDate = (Date) target;
		tmpDate.setTime(time);
	}
	
}
