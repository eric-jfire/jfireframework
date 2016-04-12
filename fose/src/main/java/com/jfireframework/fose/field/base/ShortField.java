package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class ShortField extends CacheField
{
	public ShortField(Field field)
	{
		super(field);
	}
	
	public ShortField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public ShortField(long offset, int dim)
	{
		super(offset, dim, short.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -17;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putShort(host, offset, buf.readShort());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeShort(unsafe.getShort(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		short[] value = (short[]) array;
		buf.writeShortArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readShortArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
}
