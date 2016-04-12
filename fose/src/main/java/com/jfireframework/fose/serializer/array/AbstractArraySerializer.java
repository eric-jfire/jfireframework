package com.jfireframework.fose.serializer.array;

import java.lang.reflect.Array;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.serializer.Serializer;
import com.jfireframework.fose.util.DimensionUtil;

public abstract class AbstractArraySerializer implements Serializer
{
	
	@Override
	public void getObjects(Object src, ObjectCollect collect)
	{
		collect.add(src);
	}
	
	@Override
	public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		int dim = DimensionUtil.getDimByComponent(src.getClass());
		writeArray(src, dim, buf, true, collect);
	}
	
	private void writeArray(Object src, int dimension, ByteBuf<?> buf, boolean first, ObjectCollect collect)
	{
		if (src == null)
		{
			buf.writeInt(-1);
			return;
		}
		// 如果已经是一维数组，则将数组内容输出
		if (dimension == 1)
		{
			writeOneDimensionMember(src, buf, first, collect);
		}
		// 如果不是一维数组，则将数组的每一个内容继续该循环
		else
		{
			dimension--;
			// 非一维数组都可以表示为一个object，同时低一维的数组且非一维数组也是一个object
			Object[] array = (Object[]) src;
			int length = array.length;
			// 如果是最开始的那个数组，因为在序列化的时候已经写入了长度，所以这里长度是不写的，而后面的都是要写的。也就是多维数组的低维子数组的长度要写入
			if (first == false)
			{
				buf.writeInt(length);
			}
			for (int i = 0; i < length; i++)
			{
				writeArray(array[i], dimension, buf, false, collect);
			}
		}
	}
	
	/**
	 * 将一维数组src写入。first代表是否是最开始的数组。如果是最开始的数组，则不需要写入数组长度，因为在序列化的时候已经写入过了。如果不是，
	 * 则需要写入数组长度
	 * 
	 * @param src
	 * @param cache
	 * @param first
	 */
	protected abstract void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect);
	
	@SuppressWarnings("rawtypes")
	@Override
	public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		int dim = 0;
		Class<?> tmpClass = target.getClass();
		while (tmpClass.isArray())
		{
			dim++;
			tmpClass = tmpClass.getComponentType();
		}
		Class[] arrayTypes = new Class[dim];
		arrayTypes[0] = tmpClass;
		for (int i = 1; i < dim; i++)
		{
			arrayTypes[i] = Array.newInstance(tmpClass, new int[i]).getClass();
		}
		if (dim > 1)
		{
			Object[] array = (Object[]) target;
			int length = array.length;
			dim--;
			for (int i = 0; i < length; i++)
			{
				array[i] = readArray(dim, buf, collect, arrayTypes);
			}
		}
		else
		{
			readOneDimensionMember(target, null, buf, collect, tmpClass);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private Object readArray(int dimension, ByteBuf<?> buf, ObjectCollect collect, Class[] arrayTypes)
	{
		int length = buf.readInt();
		if (length == -1)
		{
			return null;
		}
		// 如果维度是1，则按照个数读取内容，并且组装一维成数组返回
		if (dimension == 1)
		{
			return readOneDimensionMember(null, length, buf, collect, arrayTypes[0]);
		}
		else
		{
			// 如果维度不是1，则分别读取每一个多维数组，采用递归的方式进行
			dimension--;
			Object[] arrayObject = (Object[]) Array.newInstance(arrayTypes[dimension], length);
			for (int i = 0; i < length; i++)
			{
				arrayObject[i] = readArray(dimension, buf, collect, arrayTypes);
			}
			return arrayObject;
		}
	}
	
	/**
	 * 读取cache内容，如果target不是null，就将读取的数组内容填充到target中。否则就新建一个长度为length的数组并且填充内容后返回
	 * 
	 * @param target
	 * @param length
	 * @param cache
	 * @param rootType TODO
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType);
}
