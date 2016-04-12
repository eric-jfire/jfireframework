package com.jfireframework.fose.field.special;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;

@SuppressWarnings("restriction")
public class StringField extends CacheField
{
	public StringField(Field field)
	{
		super(field);
	}
	
	public StringField(long offset, Class<?> type)
	{
		super(offset, type);
	}
	
	public StringField(long offset, int dim)
	{
		super(offset, dim, String.class);
	}
	
	@Override
	public int getSerNo()
	{
		return -18;
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		unsafe.putObject(host, offset, buf.readString());
	}
	
	@Override
	protected void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		String tmp = (String) unsafe.getObject(host, offset);
		buf.writeString(tmp);
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
	}
	
	@Override
	protected void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		String[] arrayString = (String[]) array;
		buf.writeInt(arrayString.length);
		for (String each : arrayString)
		{
			buf.writeString(each);
		}
	}
	
	@Override
	protected Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		String[] array = new String[length];
		for (int i = 0; i < length; i++)
		{
			array[i] = buf.readString();
		}
		return array;
	}
}
