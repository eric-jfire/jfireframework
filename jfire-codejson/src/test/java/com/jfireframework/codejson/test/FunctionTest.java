package com.jfireframework.codejson.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import com.jfireframework.baseutil.reflect.TypeUtil;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.test.strategy.FunData16;
import com.jfireframework.codejson.test.strategy.FunctionData10;
import com.jfireframework.codejson.test.strategy.FunctionData13;
import com.jfireframework.codejson.test.strategy.FunctionData7;
import com.jfireframework.codejson.test.strategy.TestEnum;
import com.jfireframework.codejson.util.NameTool;

public class FunctionTest extends Support
{
    
    @Test
    public void rightTest()
    {
        String string = JsonTool.write(data);
        logger.debug("输出的json是\n\n{}\n\n", string);
        assertTrue(data.equal(JsonTool.read(Data.class, string)));
        logger.debug("输出的数组json是\n\n{}\n\n", JsonTool.write(new Data[] { data, data }));
        Data[][] origin = new Data[][] { { data, data }, { data, data, data } };
        string = JsonTool.write(origin);
        Data[][] result = JsonTool.read(Data[][].class, string);
        for (int i = 0; i < origin.length; i++)
        {
            for (int j = 0; j < origin[i].length; j++)
            {
                assertTrue(origin[i][j].equal(result[i][j]));
            }
        }
        Data[] test1 = new Data[] { data, data };
        string = JsonTool.write(test1);
        Data[] test1Result = JsonTool.read(Data[].class, string);
        for (int i = 0; i < test1.length; i++)
        {
            assertTrue(test1[i].equal(test1Result[i]));
        }
        Data[][][] test2 = new Data[][][] { { { data, data }, { data } }, { { data, data, data, data }, { data }, { data } } };
        string = JsonTool.write(test2);
        Data[][][] test2Result = JsonTool.read(Data[][][].class, string);
        for (int i = 0; i < test2Result.length; i++)
        {
            for (int j = 0; j < test2Result[i].length; j++)
            {
                for (int k = 0; k < test2Result[i][j].length; k++)
                {
                    assertTrue(test2Result[i][j][k].equal(test2[i][j][k]));
                }
            }
        }
    }
    
    @Test
    public void typeTest()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("sdadasd");
        list.add("sdadsasda");
        String value = JsonTool.write(list);
        ArrayList<String> result = (ArrayList<String>) JsonTool.read(new TypeUtil<ArrayList<String>>() {}.getType(), value);
        assertTrue(list.equals(result));
        ArrayList<NestData> arrayList = new ArrayList<NestData>();
        NestData cdata = new NestData();
        cdata.setAge(1212);
        cdata.setName("sdasdas");
        arrayList.add(cdata);
        cdata = new NestData();
        cdata.setAge(1212121);
        cdata.setName("dassdas");
        arrayList.add(cdata);
        value = JsonTool.write(arrayList);
        System.out.println(value);
        ArrayList<NestData> result1 = JsonTool.read(new TypeUtil<ArrayList<NestData>>() {}.getType(), value);
        assertTrue(arrayList.equals(result1));
        ArrayList<Data> list2 = new ArrayList<Data>();
        list2.add(data);
        list2.add(data);
        value = JsonTool.toString(list2);
        ArrayList<Data> result3 = JsonTool.read(new TypeUtil<ArrayList<Data>>() {}.getType(), value);
        assertTrue(list2.get(0).equal(result3.get(0)) && list2.get(1).equal(result3.get(1)));
        HashMap<String, Data> map = new HashMap<String, Data>();
        map.put("12wq", data);
        map.put("xczc", data);
        value = JsonTool.write(map);
        HashMap<String, Data> result4 = JsonTool.read(new TypeUtil<HashMap<String, Data>>() {}.getType(), value);
        assertTrue(map.get("12wq").equal(result4.get("12wq")));
        assertTrue(map.get("xczc").equal(result4.get("xczc")));
    }
    
    @Test
    public void fileTest() throws URISyntaxException, IOException
    {
        File configFile = new File(this.getClass().getClassLoader().getResource("config.json").toURI());
        FileInputStream inputStream = new FileInputStream(configFile);
        byte[] array = new byte[inputStream.available()];
        inputStream.read(array);
        String jsonStr = new String(array);
        System.out.println(jsonStr);
        JsonObject jsonObject = (JsonObject) JsonTool.fromString(jsonStr);
        System.out.println(JsonTool.write(jsonObject));
    }
    
    @Test
    public void test1()
    {
        assertEquals("{\"data\":{\"1\":\"121212\"}}", JsonTool.write(new FunctionData7()));
    }
    
    @Test
    public void test2()
    {
        new NameTool();
        System.out.println(JsonTool.write(new FunctionData10()));
    }
    
    @Test
    public void test3()
    {
        assertEquals("{\"array1\":[\"1212\",\"12112\"],\"array2\":[1,2,3],\"array3\":[true,false],\"array4\":[\"c\",\"d\"],\"array5\":[1,2,3,4,5,7],\"array6\":[1221121231231,212312313],\"array7\":[2.36,5.698],\"array8\":[2323.231,2323.2313123],\"array9\":[100,23]}", JsonTool.write(new FunctionData13()));
    }
    
    @Test
    public void enumTest()
    {
        FunData16 data16 = new FunData16();
        data16.setTest(TestEnum.PUSH);
        assertEquals("{\"test\":\"PUSH\"}", JsonTool.write(data16));
        WriteStrategy strategy = new WriteStrategy();
        strategy.setWriteEnumName(false);
        assertEquals("{\"test\":0}", strategy.write(data16));
        strategy = new WriteStrategy();
        assertEquals("{\"test\":\"PUSH\"}", strategy.write(data16));
        FunData16 result = JsonTool.read(FunData16.class, "{\"test\":\"PUSH\"}");
        assertEquals(TestEnum.PUSH, result.getTest());
        ReadStrategy readStrategy = new ReadStrategy();
        result = readStrategy.read(FunData16.class, "{\"test\":\"PUSH\"}");
        assertEquals(TestEnum.PUSH, result.getTest());
        readStrategy = new ReadStrategy();
        readStrategy.setReadEnumName(false);
        result = readStrategy.read(FunData16.class, "{\"test\":0}");
        assertEquals(TestEnum.PUSH, result.getTest());
        
    }
}
