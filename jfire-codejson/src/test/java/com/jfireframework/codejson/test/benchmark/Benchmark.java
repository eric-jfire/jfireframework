package com.jfireframework.codejson.test.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.test.Data;
import com.jfireframework.codejson.test.NestData;

public class Benchmark
{
    
    private Logger      logger = ConsoleLogFactory.getLogger();
    private SmallObject smallData;
    private BigData     bigData;
    
    @Before
    public void Before()
    {
        smallData = new SmallObject();
        smallData.setA(1);
        smallData.setA1(12);
        smallData.setAge(12);
        smallData.setB(5.6f);
        smallData.setB1(2.36f);
        smallData.setC(2.3659);
        smallData.setC1(2.3656);
        smallData.setD(56676416847694l);
        smallData.setD1(12312312l);
        smallData.setE('e');
        smallData.setE1("2ewaedasdas");
        smallData.setF(true);
        /********/
        bigData = new BigData();
        bigData.setA(12);
        bigData.setB(2.36f);
        bigData.setC(5.6987);
        bigData.setD(121212121212l);
        bigData.setE('f');
        bigData.setF(true);
        bigData.setG((short) 5.689);
        bigData.setH((byte) 3);
        bigData.setA1(1);
        bigData.setB1(2.34f);
        bigData.setC1(2323.34234234);
        bigData.setD1(11231231231313133l);
        bigData.setE1("sdasdasd");
        bigData.setF1(true);
        bigData.setG1((short) 2);
        bigData.setH1((byte) 12);
        NestData nestData = new NestData();
        nestData.setName("dsadas");
        bigData.setNestData(nestData);
        ArrayList<String> list = new ArrayList<>();
        list.add("husdasdad");
        list.add("siudsan");
        bigData.setList(list);
        ArrayList<NestData> nestDatas = new ArrayList<>();
        nestData = new NestData();
        nestData.setName("sdasda");
        nestData.setAge(13);
        nestDatas.add(nestData);
        nestData = new NestData();
        nestData.setName("dasdas");
        nestData.setAge(20);
        nestDatas.add(nestData);
        bigData.setDatas(nestDatas);
        bigData.setNolist(new ArrayList<String>());
        HashMap<String, String> map = new HashMap<>();
        map.put("恁大", "dasdasd");
        map.put("dsada", "你好");
        bigData.setMap(map);
        bigData.setArray2(new int[][] { { 1, 2, 3, 4 }, { 10, 12 } });
        bigData.setStrs(new String[] { "231231", "sdadsasdasd" });
        bigData.setArray1(new int[] { 1, 2, 3, 4, 5, 65 });
        bigData.setChars(new char[] { 'a', 'b' });
        bigData.setArray3(new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
        bigData.setArray4(new Integer[][] { { 1, 2, 3, 4, 5, 56 }, { 10, 11, 12, 14 } });
        NestData[] nestDatas2 = new NestData[2];
        NestData tmp = new NestData();
        tmp.setAge(12);
        tmp.setName("das");
        nestDatas2[0] = tmp;
        tmp = new NestData();
        tmp.setAge(1222);
        tmp.setName("daasdadasd");
        nestDatas2[1] = tmp;
        bigData.setNestDatas(nestDatas2);
        HashMap<Date, NestData> map2 = new HashMap<>();
        map2.put(new Date(), tmp);
        @SuppressWarnings("unchecked")
        ArrayList<String>[] lists = new ArrayList[] { new ArrayList<>(), new ArrayList<>() };
        lists[0].add("dasdasda");
        lists[0].add("dasdasdasdasdasd");
        lists[1].add("1212121dasdasdasdasdasd");
        lists[1].add("dasdasd1212121212asdasdasd");
        bigData.setLists(lists);
        bigData.setData(nestDatas2);
    }
    
    @Test
    public void small() throws JsonProcessingException
    {
        int count = 1000000;
        ObjectMapper mapper = new ObjectMapper();
        JsonTool.write(smallData);
        JSON.toJSONString(smallData);
        mapper.writeValueAsString(smallData);
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.toJSONString(smallData);
        }
        timewatch.end();
        logger.info("fastjson小对象序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JsonTool.write(smallData);
        }
        timewatch.end();
        logger.info("codejson小对象序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            mapper.writeValueAsString(smallData);
        }
        timewatch.end();
        logger.info("jackson2小对象序列化耗时：{}", timewatch.getTotal());
    }
    
    @Test
    public void smallParse() throws JsonParseException, JsonMappingException, IOException
    {
        int count = 1000000;
        String value = JsonTool.write(smallData);
        JsonTool.read(SmallObject.class, value);
        JSON.parseObject(value, SmallObject.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(value, SmallObject.class);
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.parseObject(value, SmallObject.class);
        }
        timewatch.end();
        logger.info("fastjson小json反序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JsonTool.read(SmallObject.class, value);
        }
        timewatch.end();
        logger.info("codejson小json反序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.parseObject(value, SmallObject.class);
        }
        timewatch.end();
        logger.info("jackson2小json反序列化耗时：{}", timewatch.getTotal());
    }
    
    @Test
    public void big() throws JsonProcessingException
    {
        int count = 1000000;
        ObjectMapper mapper = new ObjectMapper();
        JSON.toJSONString(bigData);
        JsonTool.write(bigData);
        WriteStrategy writeStrategy = new WriteStrategy();
        writeStrategy.setUseTracker(true);
        writeStrategy.write(bigData);
        mapper.writeValueAsString(bigData);
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.toJSONString(bigData,SerializerFeature.DisableCircularReferenceDetect);
        }
        timewatch.end();
        logger.info("fastjson大对象序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JsonTool.write(bigData);
//            writeStrategy.write(bigData);
        }
        timewatch.end();
        logger.info("codejson大对象序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            mapper.writeValueAsString(bigData);
        }
        timewatch.end();
        logger.info("jackson2大对象序列化耗时：{}", timewatch.getTotal());
    }
    
    @Test
    public void bigParse() throws JsonParseException, JsonMappingException, IOException
    {
        int count = 100000;
        String value = JsonTool.write(bigData);
        BigData result = JSON.parseObject(value, BigData.class);
        System.out.println(result.equal(bigData));
        result = JsonTool.read(BigData.class, value);
        System.out.println(result.equal(bigData));
        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(value, BigData.class);
        System.out.println(result.equal(bigData));
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
//            JSON.parseObject(value,BigData.class);
        }
        timewatch.end();
        logger.info("fastjson大json序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
//            JsonTool.read(BigData.class, value);
            JsonTool.fromString(value);
        }
        timewatch.end();
        logger.info("codejson大json序列化耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
//            mapper.readValue(value, BigData.class);
            mapper.readTree(value);
        }
        timewatch.end();
        logger.info("jackson2大json序列化耗时：{}", timewatch.getTotal());
    }
}
