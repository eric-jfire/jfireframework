package com.jfireframework.fose.field.special;

import java.lang.reflect.Field;
import java.util.Date;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class DateField extends CacheField
{
	
	public DateField(Field field)
	{
		super(field);
	}
	
	@Override
	public int getSerNo()
	{
		return -19;
	}
	
	@Override
	protected void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		long time = buf.readMutableLengthLong();
		if (time == -1)
		{
			unsafe.putObject(host, offset, null);
		}
		else
		{
			unsafe.putObject(host, offset, new Date(time));
		}
	}
	
	@Override
	protected void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Date date = (Date) unsafe.getObject(host, offset);
		if (date == null)
		{
			buf.writeMutableLengthLong(-1);
			return;
		}
		long time = date.getTime();
		buf.writeMutableLengthLong(time);
	}
	
	@Override
	protected void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Date[] arrayDate = (Date[]) array;
		buf.writeInt(arrayDate.length);
		for (Date each : arrayDate)
		{
			if (each == null)
			{
				buf.writeMutableLengthLong(-1);
			}
			else
			{
				buf.writeMutableLengthLong(each.getTime());
			}
		}
	}
	
	@Override
	protected Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Date[] array = new Date[length];
		for (int i = 0; i < length; i++)
		{
			long time = buf.readMutableLengthLong();
			if (length == -1)
			{
				array[i] = null;
			}
			else
			{
				array[i] = new Date(time);
			}
		}
		return array;
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
		// TODO Auto-generated method stub
		
	}
	
}
