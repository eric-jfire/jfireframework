package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class IntField extends CacheField
{
	public IntField(Field field)
	{
		super(field);
	}
	
	public IntField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public IntField(long offset, int dim)
	{
		super(offset, dim, int.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -15;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putInt(host, offset, buf.readInt());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeInt(unsafe.getInt(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object arrayObject, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		int[] value = (int[]) arrayObject;
		buf.writeIntArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readIntArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
}
