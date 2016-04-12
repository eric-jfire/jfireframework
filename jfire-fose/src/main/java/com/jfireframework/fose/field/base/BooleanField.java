package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class BooleanField extends CacheField
{
	public BooleanField(Field field)
	{
		super(field);
	}
	
	public BooleanField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public BooleanField(long offset, int dim)
	{
		super(offset, dim, boolean.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -10;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		if (buf.get() > 0)
		{
			unsafe.putBoolean(host, offset, true);
		}
		else
		{
			unsafe.putBoolean(host, offset, false);
		}
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeBoolean(unsafe.getBoolean(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		boolean[] value = (boolean[]) array;
		buf.writeBooleanArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readBooleanArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
		
	}
	
}
