package com.jfireframework.fose;

import static org.junit.Assert.assertEquals;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jfire.test.rule.CustomRule;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.encrypt.AesUtil;
import com.jfireframework.fose.Fose;
import com.jfireframework.fose.data.ArrayData;
import com.jfireframework.fose.data.BaseData;
import com.jfireframework.fose.data.LongData;
import com.jfireframework.fose.data.Person;
import com.jfireframework.fose.data.WrapData;

public class RightTest
{
	ByteBuf<?>			buf		= HeapByteBufPool.getInstance().get(100);
	
	@Rule
	public CustomRule	rule	= new CustomRule();
	
	@Test
	public void baseTypeTest() throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException, ClassNotFoundException, InstantiationException
	{
		buf.clear();
		// 创建需要序列化的对象
		BaseData baseData = new BaseData(1);
		// 构建lbse对象，该对象是非线程安全的，请注意
		Fose context = new Fose();
		context.register(BaseData.class);
		// 进行序列化，返回的是一个buffer对象
		context.serialize(baseData, buf);
		// 传入二进制buffer对象，读取其中的 数据并且反序列化成对象
		BaseData result = context.deserialize(buf, BaseData.class);
		assertEquals(result.getA(), baseData.getA());
		assertEquals(result.isB(), baseData.isB());
		assertEquals(result.getC(), baseData.getC());
		assertEquals(result.getD(), baseData.getD());
		assertEquals(result.getE(), baseData.getE());
		assertEquals(result.getF(), baseData.getF());
		assertEquals(result.getG(), baseData.getG(), 0.01);
		assertEquals(result.getI(), baseData.getI());
		assertEquals(result.getH(), baseData.getH(), 0.01);
		for (int i = 0; i < result.getJ().length; i++)
		{
			assertEquals(result.getJ()[i], baseData.getJ()[i]);
		}
		for (int i = 0; i < result.getK().length; i++)
		{
			assertEquals(result.getK()[i], baseData.getK()[i]);
		}
		for (int i = 0; i < result.getL().length; i++)
		{
			assertEquals(result.getL()[i], baseData.getL()[i]);
		}
		for (int i = 0; i < result.getM().length; i++)
		{
			assertEquals(result.getM()[i], baseData.getM()[i]);
		}
		for (int i = 0; i < result.getN().length; i++)
		{
			assertEquals(result.getN()[i], baseData.getN()[i]);
		}
		for (int i = 0; i < result.getO().length; i++)
		{
			assertEquals(result.getO()[i], baseData.getO()[i]);
		}
		for (int i = 0; i < result.getP().length; i++)
		{
			assertEquals(result.getP()[i], baseData.getP()[i], 0.1);
		}
		for (int i = 0; i < result.getQ().length; i++)
		{
			assertEquals(result.getQ()[i], baseData.getQ()[i], 0.1);
		}
		for (int i = 0; i < result.getR().length; i++)
		{
			assertEquals(result.getR()[i], baseData.getR()[i]);
		}
		//
	}
	
	@Test
	public void wrapTest() throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException, ClassNotFoundException, InstantiationException
	{
		buf.clear();
		WrapData wrapData = new WrapData();
		Fose context = new Fose();
		context.serialize(wrapData, buf);
		WrapData result = (WrapData) context.deserialize(buf, WrapData.class);
		assertEquals(result.getA(), wrapData.getA());
		assertEquals(result.getB(), wrapData.getB());
		assertEquals(result.getC(), wrapData.getC());
		assertEquals(result.getD(), wrapData.getD());
		assertEquals(result.getE(), wrapData.getE());
		assertEquals(result.getF(), wrapData.getF());
		assertEquals(result.getG(), wrapData.getG(), 0.01);
		assertEquals(result.getH(), wrapData.getH(), 0.01);
		assertEquals(result.getI(), wrapData.getI());
		for (int i = 0; i < result.getJ().length; i++)
		{
			assertEquals(result.getJ()[i], wrapData.getJ()[i]);
		}
		for (int i = 0; i < result.getK().length; i++)
		{
			assertEquals(result.getK()[i], wrapData.getK()[i]);
		}
		for (int i = 0; i < result.getL().length; i++)
		{
			assertEquals(result.getL()[i], wrapData.getL()[i]);
		}
		for (int i = 0; i < result.getM().length; i++)
		{
			assertEquals(result.getM()[i], wrapData.getM()[i]);
		}
		for (int i = 0; i < result.getN().length; i++)
		{
			assertEquals(result.getN()[i], wrapData.getN()[i]);
		}
		for (int i = 0; i < result.getO().length; i++)
		{
			assertEquals(result.getO()[i], wrapData.getO()[i]);
		}
		for (int i = 0; i < result.getP().length; i++)
		{
			assertEquals(result.getP()[i], wrapData.getP()[i], 0.1);
		}
		for (int i = 0; i < result.getQ().length; i++)
		{
			assertEquals(result.getQ()[i], wrapData.getQ()[i], 0.1);
		}
		for (int i = 0; i < result.getR().length; i++)
		{
			assertEquals(result.getR()[i], wrapData.getR()[i]);
		}
		for (int i = 0; i < wrapData.getList().size(); i++)
		{
			Assert.assertTrue(wrapData.getList().get(i).equals(result.getList().get(i)));
		}
		for (int i = 0; i < wrapData.getMap().size(); i++)
		{
			Assert.assertTrue(wrapData.getMap().get(i).equals(result.getMap().get(i)));
		}
		for (int i = 0; i < wrapData.getW().length; i++)
		{
			for (int j = 0; j < wrapData.getW()[i].length; j++)
			{
				Assert.assertEquals(result.getW()[i][j], wrapData.getW()[i][j]);
			}
		}
	}
	
	@Test
	public void referenceTest() throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException, ClassNotFoundException, InstantiationException
	{
		Person person = new Person("linbin", 25);
		Person tPerson = new Person("zhangshi[in", 30);
		person.setLeader(tPerson);
		Fose context = new Fose();
		buf.clear();
		context.serialize(person, buf);
		Person result = (Person) context.deserialize(buf, Person.class);
		assertEquals("zhangshi[in", result.getLeader().getName());
	}
	
	@Test
	public void objectTest() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		Fose lbse = new Fose();
		Calendar calendar = Calendar.getInstance();
		buf.clear();
		lbse.serialize(calendar, buf);
		Calendar reCalendar = (Calendar) lbse.deserialize(buf);
		Assert.assertTrue(reCalendar.equals(calendar));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void listTest() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		ArrayList<BaseData> list = new ArrayList<>();
		for (int i = 0; i < 5; i++)
		{
			list.add(new BaseData(i));
		}
		Fose lbse = new Fose();
		buf.clear();
		lbse.serialize(list, buf);
		ArrayList<BaseData> result = (ArrayList<BaseData>) lbse.deserialize(buf);
		Assert.assertTrue(list.equals(result));
	}
	
	@Test
	public void objectArrayTest()
	{
		Object[] array = new Object[4];
		array[0] = new Person();
		array[1] = new BaseData();
		array[2] = new LongData();
		array[3] = new WrapData();
		Fose lbse = new Fose();
		lbse.serialize(array, buf.clear());
		Object[] result = (Object[]) lbse.deserialize(buf);
		Assert.assertTrue(((Person) result[0]).equals((Person) array[0]));
	}
	
	@Test
	public void byteArrayTest()
	{
		byte[] array = new byte[] { 1, 2, 5, 6, 8, 9 };
		Fose lbse = new Fose();
		lbse.serialize(array, buf.clear());
		byte[] result = (byte[]) lbse.deserialize(buf);
		for (int i = 0; i < array.length; i++)
		{
			assertEquals(array[i], result[i]);
		}
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		Output output = null;
		output = new Output(4096, 109096);
		kryo.writeClassAndObject(output, array);
		byte[] bb = output.toBytes();
		Input input = null;
		input = new Input(bb);
		result = (byte[]) kryo.readClassAndObject(input);
		for (int i = 0; i < array.length; i++)
		{
			assertEquals(array[i], result[i]);
		}
	}
	
	@Test
	public void booleanArrayTest()
	{
		boolean[] array = new boolean[] { true, false, false, true, true, true };
		Fose lbse = new Fose();
		lbse.serialize(array, buf.clear());
		boolean[] result = (boolean[]) lbse.deserialize(buf);
		for (int i = 0; i < array.length; i++)
		{
			assertEquals(array[i], result[i]);
		}
	}
	
	@Test
	public void arrayDataTest()
	{
		Fose lbse = new Fose();
		lbse.serialize(new ArrayData(), buf.clear());
		lbse.deserialize(buf);
	}
	
	@Test
	public void objectArrTest()
	{
		Random random = new Random();
		byte[] key = new byte[16];
		random.nextBytes(key);
		AesUtil aesUtil = new AesUtil(key);
		Object[] data = new Object[] { Integer.valueOf(14), new BaseData[] { new BaseData(), new BaseData() } };
		Fose lbse = new Fose();
		lbse.serialize(data, buf.clear());
		byte[] aesResult = aesUtil.encrypt(buf.toArray());
		buf.clear().put(aesUtil.decrypt(aesResult));
		Object[] result = (Object[]) lbse.deserialize(buf);
		assertEquals(14, result[0]);
		assertEquals(((BaseData[]) data[1])[0], ((BaseData[]) result[1])[0]);
		assertEquals(((BaseData[]) data[1])[1], ((BaseData[]) result[1])[1]);
	}
	
}
