package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class ShortArraySerializer extends AbstractArraySerializer
{
	@Override
	protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
	{
		short[] array = (short[]) src;
		if (first == false)
		{
			buf.writeInt(array.length);
		}
		for (short s : array)
		{
			buf.writeShort(s);
		}
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
	{
		short[] array = target == null ? new short[length] : (short[]) target;
		length = array.length;
		for (int i = 0; i < length; i++)
		{
			array[i] = buf.readShort();
		}
		return array;
	}
	
}
