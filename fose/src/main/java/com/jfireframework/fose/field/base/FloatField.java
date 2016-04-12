package com.jfireframework.fose.field.base;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class FloatField extends CacheField
{
	public FloatField(Field field)
	{
		super(field);
	}
	
	public FloatField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public FloatField(long offset, int dim)
	{
		super(offset, dim, float.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -14;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putFloat(host, offset, buf.readFloat());
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		buf.writeFloat(unsafe.getFloat(host, offset));
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		float[] value = (float[]) array;
		buf.writeFloatArray(value);
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		return buf.readFloatArray(length);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
}
