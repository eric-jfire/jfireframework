package com.jfireframework.fose;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.junit.Test;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jfireframework.baseutil.code.RandomString;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.fose.Fose;
import com.jfireframework.fose.data.Device;
import com.jfireframework.fose.data.Person;

public class SpeedTest
{
	private Logger		logger	= ConsoleLogFactory.getLogger();
	public static int	testSum	= 100000;
	private ByteBuf<?>	buf		= HeapByteBufPool.getInstance().get(100);
	
	private Device Builder()
	{
		Device device = new Device();
		device.setActivationTime(new Date());
		device.setBound(true);
		device.setBuildVersion(1);
		device.setId(9876543210L);
		device.setIdfa("照片没问wqeqw");
		device.setImei("照片没wewqe问");
		device.setMac("照qw片没问");
		device.setMajorVersion(3);
		device.setMinorVersion(6);
		device.setOpenUdid(RandomString.randomString(48));
		device.setOs(3);
		device.setOsVersion("照qwqw片没问");
		device.setPromoPlatformCode(94000000);
		device.setUuid("照片没qq问");
		device.setSn(device.getOpenUdid() + "_" + device.getUuid());
		device.setUserId(1234567890L);
		return device;
	}
	
	@Test
	public void serialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException
	{
		Person person = new Person("linbin", 25);
		Person tPerson = new Person("zhangshi[in", 30);
		person.setLeader(tPerson);
		tPerson.setLeader(person);
		Device device = Builder();
		Fose context = new Fose();
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < testSum; i++)
		{
			context.serialize(person, buf.clear());
		}
		long lbseCost = System.currentTimeMillis() - t0;
		logger.info("lbse序列化耗时：{}", lbseCost);
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		Output output = null;
		output = new Output(4096, 109096);
		t0 = System.currentTimeMillis();
		for (int i = 0; i < testSum; i++)
		{
			output.clear();
			kryo.writeClassAndObject(output, person);
		}
		long kryoCost = System.currentTimeMillis() - t0;
		logger.info("kryo序列化耗时{}", kryoCost);
		logger.info("lbse比kryo快{},性能比是{}", (kryoCost - lbseCost), ((float) lbseCost / kryoCost));
	}
	
	@Test
	public void deserialize()
	{
		Person person = new Person("linbin", 25);
		Person tPerson = new Person("zhangshi[in", 30);
		person.setLeader(tPerson);
		tPerson.setLeader(person);
		Fose context = new Fose();
		Device device = Builder();
		context.serialize(person, buf.clear());
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < testSum; i++)
		{
			buf.readIndex(0);
			context.deserialize(buf);
		}
		long lbseCost = System.currentTimeMillis() - t0;
		logger.info("lbse逆序列化耗时：{}", lbseCost);
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		Output output = null;
		output = new Output(4096, 109096);
		output.clear();
		kryo.writeClassAndObject(output, person);
		byte[] bb = output.toBytes();
		t0 = System.currentTimeMillis();
		for (int i = 0; i < testSum; i++)
		{
			Input input = null;
			input = new Input(bb);
			kryo.readClassAndObject(input);
		}
		long kryoCost = System.currentTimeMillis() - t0;
		logger.info("kryo逆序列化耗时{}", kryoCost);
		logger.info("lbse比kryo快{},性能比是{}", (kryoCost - lbseCost), ((float) lbseCost / kryoCost));
	}
}
