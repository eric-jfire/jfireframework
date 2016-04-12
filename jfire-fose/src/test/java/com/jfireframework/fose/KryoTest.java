package com.jfireframework.fose;

import java.util.Date;
import java.util.UUID;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jfireframework.baseutil.code.RandomString;
import com.jfireframework.fose.data.Device;

public class KryoTest
{
	public static void main(String[] args)
	{
		Device device = new Device();
		device.setActivationTime(new Date());
		device.setBound(true);
		device.setBuildVersion(1);
		device.setId(9876543210L);
		device.setIdfa(UUID.randomUUID().toString());
		device.setImei(RandomString.randomString(48));
		device.setMac(RandomString.randomString(12));
		device.setMajorVersion(3);
		device.setMinorVersion(6);
		device.setOpenUdid(RandomString.randomString(48));
		device.setOs(3);
		device.setOsVersion("9.1");
		device.setPromoPlatformCode(94000000);
		device.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		device.setSn(device.getOpenUdid() + "_" + device.getUuid());
		device.setUserId(1234567890L);
		
		int count = 1000_0000;
		
		Kryo kryo = new Kryo();
		long consumed = 0;
		byte[] bs = new byte[1024];
		for (int i = 0; i < count; i++)
		{
			long start = System.nanoTime();
			Output out = new Output(bs);
			kryo.writeObject(out, device);
			byte[] buf = out.toBytes();
			consumed += System.nanoTime() - start;
		}
		System.out.println("serialize:" + consumed);
		consumed = 0;
		Output out = new Output(128, 1024);
		kryo.writeObject(out, device);
		byte[] buf = out.toBytes();
		System.out.println(buf.length);
		for (int i = 0; i < count; i++)
		{
			long start = System.nanoTime();
			Input in = new Input(buf);
			Device d = kryo.readObject(in, Device.class);
			consumed += System.nanoTime() - start;
		}
		System.out.println("deserialize:" + consumed);
	}
}
/*
 * serialize: 10,386,149,412 8,603,950,420 7,209,904,072
 * deserialize:10,008,827,341 9,488,357,478 9,431,694,843
 */
/*
 * serialize: 8,845,092,992 8,846,105,138
 * deserialize:11,315,936,007 11,833,937,491
 */
