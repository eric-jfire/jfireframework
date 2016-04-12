package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class LongField extends CacheField
{
	public LongField(Field field)
	{
		super(field);
	}
	
	public LongField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public LongField(long offset, int dim)
	{
		super(offset, dim, long.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -16;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putLong(host, offset, buf.readMutableLengthLong());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeMutableLengthLong(unsafe.getLong(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		long[] value = (long[]) array;
		buf.writeLongArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readLongArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
}
