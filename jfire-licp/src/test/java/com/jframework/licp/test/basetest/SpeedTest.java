package com.jframework.licp.test.basetest;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.junit.Test;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jfireframework.baseutil.code.RandomString;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.licp.Licp;
import com.jframework.licp.test.basetest.data.BaseData;
import com.jframework.licp.test.basetest.data.Device;
import com.jframework.licp.test.basetest.data.Person;
import com.jframework.licp.test.basetest.data.SpeedData;
import com.jframework.licp.test.basetest.data.SpeedData2;
import com.jframework.licp.test.basetest.data.WrapData;

public class SpeedTest
{
    private Logger     logger  = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    public static int  testSum = 1000;
    private ByteBuf<?> buf     = HeapByteBuf.allocate(4096);
    
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
    public void longtest()
    {
        Object data = new WrapData();
        Person person = new Person("linbin", 25);
        Person tPerson = new Person("zhangshi[in", 30);
        person.setLeader(tPerson);
        tPerson.setLeader(person);
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        Output output = null;
        output = new Output(4096, 109096);
        kryo.writeClassAndObject(output, data);
        System.out.println(output.toBytes().length);
        Licp licp = new Licp();
        ByteBuf<?> buf = HeapByteBuf.allocate(4058);
        licp.serialize(data, buf);
        System.out.println(buf.toArray().length);
    }
    
    @Test
    public void serialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException, UnsupportedEncodingException, NoSuchFieldException, SecurityException, IllegalArgumentException
    {
        int testSum = 1000;
        Person person = new Person("linbin", 25);
        Person tPerson = new Person("zhangshi[in", 30);
        person.setLeader(tPerson);
        tPerson.setLeader(person);
        Device device = Builder();
        Licp context = new Licp();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < testSum; i++)
        {
            buf.clear();
            context.serialize(person, buf);
        }
        long lbseCost = System.currentTimeMillis() - t0;
        logger.info("licp序列化耗时：{}", lbseCost);
        Kryo kryo = new Kryo();
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
        logger.info("licp比kryo快{},性能比是{}", (kryoCost - lbseCost), ((float) lbseCost / kryoCost));
        t0 = System.currentTimeMillis();
        
    }
    
    @Test
    public void ser2()
    {
        Object data = new SpeedData2();
        Licp licp = new Licp();
        int testSum = 100000;
        Kryo kryo = new Kryo();
        Output output = new Output(4096, 300000);
        kryo.writeClassAndObject(output, data);
//        System.out.println("kryo length:" + output.toBytes().length);
        System.out.println( output.toBytes().length);
        licp.serialize(data, buf);
//        System.out.println("licp length:" + buf.writeIndex());
        System.out.println( buf.writeIndex());
        Timewatch timewatch = new Timewatch();
        for (int i = 0; i < testSum; i++)
        {
            output.clear();
            kryo.writeClassAndObject(output, data);
        }
        timewatch.end();
        System.out.println("kryo:" + timewatch.getTotal());
//        System.out.println( timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < testSum; i++)
        {
            licp.serialize(data, buf.clear());
        }
        timewatch.end();
        System.out.println("licp:" + timewatch.getTotal());
//        System.out.println( timewatch.getTotal());
    }
    
    @Test
    public void deserialize()
    {
        int testSum = 1000;
        Person person = new Person("linbin", 25);
        Person tPerson = new Person("zhangshi[in", 30);
        person.setLeader(tPerson);
        tPerson.setLeader(person);
        Licp context = new Licp();
        Device device = Builder();
        context.serialize(device, buf.clear());
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < testSum; i++)
        {
            buf.readIndex(0);
            context.deserialize(buf);
        }
        long lbseCost = System.currentTimeMillis() - t0;
        logger.info("licp逆序列化耗时：{}", lbseCost);
        Kryo kryo = new Kryo();
        Output output = null;
        output = new Output(4096, 109096);
        output.clear();
        kryo.writeClassAndObject(output, device);
        byte[] bb = output.toBytes();
        Input input = null;
        input = new Input(bb);
        t0 = System.currentTimeMillis();
        for (int i = 0; i < testSum; i++)
        {
            input.setPosition(0);
            kryo.readClassAndObject(input);
        }
        long kryoCost = System.currentTimeMillis() - t0;
        logger.info("kryo逆序列化耗时{}", kryoCost);
        logger.info("licp比kryo快{},性能比是{}", (kryoCost - lbseCost), ((float) lbseCost / kryoCost));
    }
}
