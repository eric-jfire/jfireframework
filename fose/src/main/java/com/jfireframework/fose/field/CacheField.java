package com.jfireframework.fose.field;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.fose.ClassNoRegister;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class CacheField
{
	protected Field				field;
	// 如果属性是数组，则是最底层的类型，否则就是属性的类型
	protected Class<?>			rootType;
	protected long				offset;
	// 如果该属性是数组，则该数字代表数组维度，一维数组数组维度是1
	protected int				dimension	= 0;
	protected static Unsafe		unsafe		= ReflectUtil.getUnsafe();
	protected static Charset	charset		= Charset.forName("utf8");
	@SuppressWarnings("rawtypes")
	protected Class[]			arrayTypes;
	
	/**
	 * 初始化cachefield，主要确定属性的类型，确定数组维度（非数组维度是0），确定属性的offset
	 * 
	 * @param field
	 */
	public CacheField(Field field)
	{
		this.field = field;
		offset = unsafe.objectFieldOffset(field);
		rootType = field.getType();
		while (rootType.isArray())
		{
			rootType = rootType.getComponentType();
			dimension++;
		}
		arrayTypes = new Class[dimension + 1];
		for (int i = 1; i < dimension + 1; i++)
		{
			arrayTypes[i] = Array.newInstance(rootType, new int[i]).getClass();
		}
	}
	
	public CacheField(long offset, Class<?> type)
	{
		this.offset = offset;
		rootType = type;
		while (rootType.isArray())
		{
			rootType = rootType.getComponentType();
			dimension++;
		}
		arrayTypes = new Class[dimension + 1];
		for (int i = 1; i < dimension + 1; i++)
		{
			arrayTypes[i] = Array.newInstance(rootType, new int[i]).getClass();
		}
	}
	
	public CacheField(long offset, int dim, Class<?> rootType)
	{
		this.offset = offset;
		this.rootType = rootType;
		dimension = dim;
		arrayTypes = new Class[dimension + 1];
		for (int i = 1; i < dimension + 1; i++)
		{
			arrayTypes[i] = Array.newInstance(rootType, new int[i]).getClass();
		}
	}
	
	public long getOffset()
	{
		return offset;
	}
	
	public void setOffset(long offset)
	{
		this.offset = offset;
	}
	
	public int getDim()
	{
		return dimension;
	}
	
	public void setDim(int dim)
	{
		dimension = dim;
	}
	
	abstract public int getSerNo();
	
	/**
	 * 从buffer中读取字节并且转化为内容设置到对应的属性值
	 * 
	 * @param host
	 * @param cache
	 * @param register TODO
	 * @throws InstantiationException
	 */
	public void read(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		if (dimension > 0)
		{
			unsafe.putObject(host, offset, readArray(dimension, buf, collect, register));
		}
		else
		{
			readSingle(host, buf, collect, register);
		}
	}
	
	/**
	 * 将属性值写入buffer中
	 * 
	 * @param host
	 * @param cache
	 * @param register TODO
	 */
	public void write(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		if (dimension > 0)
		{
			Object arrayObject = unsafe.getObject(host, offset);
			writeArray(arrayObject, dimension, buf, collect, register);
		}
		else
		{
			writeSingle(host, buf, collect, register);
		}
	}
	
	/**
	 * 从缓冲区读取内容，并且组装成数组返回
	 * 
	 * @param dimension 当前数组的维度
	 * @param cache
	 * @param register TODO
	 * @return
	 * @throws InstantiationException
	 */
	private Object readArray(int dimension, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		int length = buf.readInt();
		if (length == -1)
		{
			return null;
		}
		// 如果维度是1，则按照个数读取内容，并且组装一维成数组返回
		if (dimension == 1)
		{
			return readOneDimensionMember(length, buf, collect, register);
		}
		else
		{
			// 如果维度不是1，则分别读取每一个多维数组，采用递归的方式进行
			Object[] arrayObject = (Object[]) Array.newInstance(arrayTypes[dimension - 1], length);
			for (int i = 0; i < length; i++)
			{
				arrayObject[i] = readArray(dimension - 1, buf, collect, register);
			}
			return arrayObject;
		}
	}
	
	/**
	 * 非数组情况下，读取属性值,并且赋值到src的对应区域
	 * 
	 * @param host 属性值所在的对象
	 * @param cache
	 * @param register TODO
	 */
	protected abstract void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
	/**
	 * 将单一值吸入cache中。host为宿主对象实例
	 * 
	 * @param host
	 * @param cache
	 * @param collect
	 * @param register TODO
	 */
	protected abstract void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
	/**
	 * 将数组按照一定规则输出到缓冲区中
	 * 
	 * @param arrayObject
	 *            数组对象
	 * @param dimension
	 *            该数组维度
	 * @param cache
	 *            输出缓冲区
	 * @param register TODO
	 */
	private void writeArray(Object arrayObject, int dimension, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		if (arrayObject == null)
		{
			buf.writeInt(-1);
			return;
		}
		// 如果已经是一维数组，则将数组内容输出
		if (dimension == 1)
		{
			writeOneDimensionMember(arrayObject, buf, collect, register);
		}
		// 如果不是一维数组，则将数组的每一个内容继续该循环
		else
		{
			dimension--;
			// 非一维数组都可以表示为一个object，同时低一维的数组且非一维数组也是一个object
			Object[] array = (Object[]) arrayObject;
			int length = array.length;
			buf.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				writeArray(array[i], dimension, buf, collect, register);
			}
		}
	}
	
	/**
	 * 将一位数组写入缓存中
	 * 
	 * @param array
	 * @param cache
	 * @param register TODO
	 */
	protected abstract void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
	/**
	 * 从缓存中读取一维数组，数组内容长度为length
	 * 
	 * @param length
	 * @param cache
	 * @param register TODO
	 * @return
	 */
	protected abstract Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
	public abstract void getObjects(Object host, ObjectCollect collect);
}
