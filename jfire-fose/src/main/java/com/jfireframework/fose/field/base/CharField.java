package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class CharField extends CacheField
{
	public CharField(Field field)
	{
		super(field);
	}
	
	public CharField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public CharField(long offset, int dim)
	{
		super(offset, dim, char.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -12;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putChar(host, offset, buf.readChar());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeChar(unsafe.getChar(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		char[] value = (char[]) array;
		buf.writeCharArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readCharArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
}
