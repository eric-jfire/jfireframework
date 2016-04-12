package com.jfireframework.fose.field;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.BeanSerializerFactory;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.base.BooleanField;
import com.jfireframework.fose.field.base.ByteField;
import com.jfireframework.fose.field.base.CharField;
import com.jfireframework.fose.field.base.DoubleField;
import com.jfireframework.fose.field.base.FloatField;
import com.jfireframework.fose.field.base.IntField;
import com.jfireframework.fose.field.base.LongField;
import com.jfireframework.fose.field.base.ShortField;
import com.jfireframework.fose.field.special.StringField;

@SuppressWarnings("restriction")
public class DirectObjectField extends CacheField
{
	// 某个类型是否是可以继续分拆拿出更多的object
	private static Map<Class<?>, Boolean>	resultMap		= new ConcurrentHashMap<>();
	// 某个类型的维度
	private static Map<Class<?>, Integer>	cacheDimMap		= new ConcurrentHashMap<>();
	// 某个类型所使用的基本field来分析
	private static Map<Class<?>, Integer>	fieldIndexMap	= new ConcurrentHashMap<>();
	private CacheField[]					replaceFields;
	
	public DirectObjectField(Field field)
	{
		super(field);
		replaceFields = new CacheField[] { new BooleanField(offset, 0), new ByteField(offset, 0), new CharField(offset, 0), new DoubleField(offset, 0), new FloatField(offset, 0), new IntField(offset, 0), new LongField(offset, 0), new ShortField(offset, 0), new StringField(offset, 0)
		};
	}
	
	@Override
	public int getSerNo()
	{
		throw new RuntimeException("不应该使用到这里");
	}
	
	@Override
	public void getObjects(Object host, ObjectCollect collect)
	{
		if (dimension > 0)
		{
			putEachSingleInCollectFromArray(collect, unsafe.getObject(host, offset), dimension);
		}
		else
		{
			Object value = unsafe.getObject(host, offset);
			if (value != null)
			{
				Class<?> target = value.getClass();
				if (hasObjectPut(target))
				{
					BeanSerializerFactory.getSerializer(target).getObjects(value, collect);
				}
				
			}
		}
	}
	
	private boolean hasObjectPut(Class<?> target)
	{
		Boolean result = resultMap.get(target);
		if (result != null)
		{
			return result;
		}
		Class<?> origin = target;
		int dim = 0;
		while (target.isArray())
		{
			target = target.getComponentType();
			dim++;
		}
		if (target.isPrimitive() || target.equals(String.class))
		{
			if (target.equals(boolean.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 1);
			}
			else if (target.equals(byte.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 2);
			}
			else if (target.equals(char.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 3);
			}
			else if (target.equals(double.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 4);
			}
			else if (target.equals(float.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 5);
			}
			else if (target.equals(int.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 6);
			}
			else if (target.equals(long.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 7);
			}
			else if (target.equals(short.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 8);
			}
			else if (target.equals(String.class))
			{
				cacheDimMap.put(origin, dim);
				fieldIndexMap.put(origin, 9);
			}
			resultMap.put(origin, false);
			return false;
		}
		else
		{
			resultMap.put(origin, true);
			return true;
		}
	}
	
	/**
	 * 对数组不断进行分解，直到将数组中每一个单一元素均放入collect中
	 * 
	 * @param collection
	 * @param array
	 * @param dimension
	 */
	protected void putEachSingleInCollectFromArray(ObjectCollect collect, Object array, int dimension)
	{
		if (array == null)
		{
			return;
		}
		Object[] value = (Object[]) array;
		int length = value.length;
		if (dimension == 1)
		{
			for (int i = 0; i < length; i++)
			{
				if (value[i] != null)
				{
					if (hasObjectPut(value[i].getClass()))
					{
						BeanSerializerFactory.getSerializer(value[i].getClass()).getObjects(value[i], collect);
					}
				}
			}
		}
		else
		{
			dimension--;
			for (int i = 0; i < length; i++)
			{
				putEachSingleInCollectFromArray(collect, value[i], dimension);
			}
		}
	}
	
	@Override
	public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		int value = buf.readInt();
		if (value >= 0)
		{
			unsafe.putObject(host, offset, collect.get(value));
		}
		else if (value == -1)
		{
			unsafe.putObject(host, offset, null);
		}
		else
		{
			int dim = buf.readInt();
			CacheField cacheField = replaceFields[-value - 10];
			cacheField.setDim(dim);
			cacheField.read(host, buf, collect, register);
		}
	}
	
	@Override
	public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Object value = unsafe.getObject(host, offset);
		if (value == null)
		{
			buf.writeInt(-1);
			return;
		}
		Class<?> type = value.getClass();
		if (hasObjectPut(type))
		{
			buf.writeInt(collect.indexOf(unsafe.getObject(host, offset)));
		}
		else
		{
			CacheField cacheField = replaceFields[fieldIndexMap.get(type) - 1];
			cacheField.setDim(cacheDimMap.get(type));
			// 写入cachefield的seriNO，方便后续进行识别
			buf.writeInt(cacheField.getSerNo()).writeInt(cacheField.getDim());
			cacheField.write(host, buf, collect, register);
		}
	}
	
	@Override
	public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Object[] value = (Object[]) array;
		int length = value.length;
		buf.writeInt(length);
		for (int i = 0; i < length; i++)
		{
			buf.writeInt(collect.indexOf(value[i]));
		}
	}
	
	@Override
	public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
	{
		Object[] array = new Object[length];
		for (int i = 0; i < length; i++)
		{
			Object tmp = collect.get(buf.readInt());
			array[i] = tmp;
		}
		return array;
	}
	
	public Class<?> getRootType()
	{
		return Object.class;
	}
	
	public CacheField copySelf(long offset)
	{
		return null;
	}
}
