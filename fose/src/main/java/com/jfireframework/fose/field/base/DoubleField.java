package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class DoubleField extends CacheField
{
	public DoubleField(Field field)
	{
		super(field);
	}
	
	public DoubleField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public DoubleField(long offset, int dim)
	{
		super(offset, dim, double.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -13;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putDouble(host, offset, buf.readDouble());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeDouble(unsafe.getDouble(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		double[] value = (double[]) array;
		buf.writeDoubleArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readDoubleArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
		// TODO Auto-generated method stub
		
	}
	
}
