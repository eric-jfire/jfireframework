package com.jfireframework.codejson.test;

import static org.junit.Assert.assertEquals;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.test.simple.Home;
import com.jfireframework.codejson.test.simple.Person;
import com.jfireframework.codejson.test.strategy.BaseData;
import com.jfireframework.codejson.test.strategy.DateInfo;
import com.jfireframework.codejson.test.strategy.FunctionData;
import com.jfireframework.codejson.test.strategy.FunctionData11;
import com.jfireframework.codejson.test.strategy.FunctionData12;
import com.jfireframework.codejson.test.strategy.FunctionData14;
import com.jfireframework.codejson.test.strategy.FunctionData15;
import com.jfireframework.codejson.test.strategy.FunctionData2;
import com.jfireframework.codejson.test.strategy.FunctionData3;
import com.jfireframework.codejson.test.strategy.FunctionData4;
import com.jfireframework.codejson.test.strategy.FunctionData5;
import com.jfireframework.codejson.test.strategy.FunctionData6;
import com.jfireframework.codejson.test.strategy.FunctionData7;
import com.jfireframework.codejson.test.strategy.FunctionData8;
import com.jfireframework.codejson.test.strategy.FunctionData9;
import com.jfireframework.codejson.test.strategy.NestInfo;
import com.jfireframework.codejson.tracker.Tracker;

public class TestStrategy
{
    private Logger           logger = ConsoleLogFactory.getLogger();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private DateInfo         info;
    
    public TestStrategy() throws ParseException
    {
        info = new DateInfo();
        Date[] dates = new Date[] { format.parse("2015-11-14 18:00:00"), format.parse("2015-11-14 18:00:00") };
        info.setDates(dates);
        info.setDate(format.parse("2015-11-14 18:00:00"));
        NestInfo nestInfo = new NestInfo();
        nestInfo.setDate(format.parse("2015-11-14 18:00:00"));
        info.setNestInfo(nestInfo);
    }
    
    @Test
    public void testClass() throws ParseException
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.addTypeStrategy(Date.class, new WriterAdapter() {
            private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append('\"').append(format.format((Date) field)).append('\"');
            }
        });
        
        logger.info(strategy.write(info));
        String result = "{\"d\":2.3569,\"date\":\"2015-11-14\",\"dates\":[\"2015-11-14\",\"2015-11-14\"],\"nestInfo\":{\"date\":\"2015-11-14\"}}";
        assertEquals(result, strategy.write(info));
    }
    
    @Test
    public void testIgnoreAndRename()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addIgnoreField("com.jfireframework.codejson.test.strategy.DateInfo.dates");
        strategy.addRenameField("com.jfireframework.codejson.test.strategy.DateInfo.date", "date_key");
        String expect = "{\"d\":2.3569,\"date_key\":\"2015-11-14 00:00:00\",\"nestInfo\":{\"date\":\"2015-11-14 00:00:00\"}}";
        assertEquals(expect, strategy.write(info));
    }
    
    @Test
    public void testBaseFormatAndNameStrategy()
    {
        WriteStrategy strategy = new WriteStrategy();
        // 这样对所有的double输出都格式化
        strategy.addTypeStrategy(double.class, new WriterAdapter() {
            public void write(double target, StringCache cache, Object entity)
            {
                DecimalFormat format = new DecimalFormat("##.00");
                cache.append(format.format(target));
            }
        });
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.BaseData.percent", new WriterAdapter() {
            public void write(double target, StringCache cache, Object entity)
            {
                DecimalFormat format = new DecimalFormat("##.00%");
                cache.append('"').append(format.format(target / 100)).append('"');
            }
        });
        String except = "{\"a\":2.2365,\"b\":15.69,\"percent\":\"88.81%\"}";
        assertEquals(except, strategy.write(new BaseData()));
    }
    
    @Test
    public void testNest()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addRenameField("com.jfireframework.codejson.test.simple.Person.name", "myname");
        strategy.addFieldStrategy("com.jfireframework.codejson.test.simple.Person.age", new WriterAdapter() {
            public void write(int field, StringCache cache, Object entity)
            {
                cache.append(20);
            }
        });
        System.out.println(strategy.write(new Home()));
    }
    
    @Test
    public void testFunction()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData.map", new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append(((Map) field).size());
            }
        });
        strategy.addTypeStrategy(String.class, new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append("\"$").append((String) field).append("$\"");
            }
        });
        strategy.addTypeStrategy(Date.class, new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cache.append('"').append(format.format((Date) field)).append('"');
            }
        });
        String expect = "{\"map\":1,\"map2\":{\"$test$\":\"$test$\"},\"map3\":{\"1\":\"2015-11-15\"}}";
        assertEquals(expect, strategy.write(new FunctionData()));
    }
    
    @Test
    public void testFunction2()
    {
        WriteStrategy strategy = new WriteStrategy();
        // strategy.setUseTracker(true);
        strategy.addTypeStrategy(Date.class, new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cache.append('"').append(format.format((Date) field)).append('"');
            }
        });
        String except = "{\"map\":{\"test\":\"test\"},\"map2\":{\"test\":\"test\"},\"map3\":{\"1\":\"2015-11-15\"}}";
        assertEquals(except, strategy.write(new FunctionData()));
    }
    
    @Test
    public void testFunction3()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData2.age", new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append(20);
            }
        });
        System.out.println(strategy.write(new FunctionData2()));
        assertEquals("{\"age\":20,\"name\":\"林斌\"}", strategy.write(new FunctionData2()));
        strategy = new WriteStrategy();
        strategy.write(new FunctionData2());
    }
    
    @Test
    public void testFunction4()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addFieldStrategy("com.jfireframework.codejson.test.simple.Home.person", new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                Person person = (Person) field;
                cache.append('"').append(person.getName()).append(',').append(person.getAge()).append('"');
            }
        });
        assertEquals("{\"length\":50.26,\"person\":\"林斌,25\",\"wdith\":12.36}", strategy.write(new Home()));
    }
    
    @Test
    public void testFunction5()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData3.list", new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append(((List) field).size());
            }
        });
        assertEquals("{\"list\":2}", strategy.write(new FunctionData3()));
        assertEquals("{\"list\":[\"hello1\",\"hello2\"]}", JsonTool.write(new FunctionData3()));
        strategy = new WriteStrategy();
        strategy.addTypeStrategy(String.class, new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append("\"$").append((String) field).append('"');
            }
        });
        assertEquals("{\"list\":[\"$hello1\",\"$hello2\"]}", strategy.write(new FunctionData3()));
    }
    
    @Test
    public void testFunction6()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.addTypeStrategy(String.class, new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append('"').append('$').append((String) field).append('"');
            }
        });
        String expect = "{\"b\":false,\"bb\":110,\"c\":\"H\",\"d\":2.3569,\"f\":2.232,\"i\":2231231,\"l\":12213123123,\"s\":12312,\"ss\":\"$林斌\",\"sss\":\"$sdadas\"}";
        assertEquals(expect, strategy.write(new FunctionData4()));
    }
    
    @Test
    public void testFunction7()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.addTypeStrategy(String.class, new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append('"').append('$').append((String) field).append('"');
            }
        });
        String expect = "{\"listArrays\":[[\"dsads\",\"dsadssdsasdas\"],[\"ds1212s\",\"d121212dsasdas\"]]}";
        assertEquals(expect, strategy.write(new FunctionData5()));
    }
    
    @Test
    public void testFunction8()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        assertEquals("{\"maps\":[{\"test\":\"test\"},{\"abc\":\"def\"}]}", strategy.write(new FunctionData6()));
        assertEquals("{\"maps\":[{\"test\":\"test\"},{\"abc\":\"def\"}]}", JsonTool.write(new FunctionData6()));
        
    }
    
    @Test
    public void testFunction9()
    {
        WriteStrategy strategy = new WriteStrategy();
        assertEquals("{\"data\":{\"1\":\"121212\"}}", strategy.write(new FunctionData7()));
    }
    
    @Test
    public void test10()
    {
        WriteStrategy strategy = new WriteStrategy();
        assertEquals("{\"data\":{\"你好\":\"林斌\"}}", strategy.write(new FunctionData8()));
        strategy = new WriteStrategy();
        strategy.addTypeStrategy(String.class, new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append("\"$").append((String) field).append('"');
            }
        });
        assertEquals("{\"data\":{\"$你好\":\"$林斌\"}}", strategy.write(new FunctionData8()));
    }
    
    @Test
    public void test11()
    {
        WriteStrategy strategy = new WriteStrategy();
        System.out.println(strategy.write(new FunctionData9()));
        assertEquals("{\"data\":{\"sda\":\"2015-11-16 00:00:00\"}}", strategy.write(new FunctionData9()));
    }
    
    @Test
    public void test12()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData11.list", new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                List<String> list = (List<String>) field;
                cache.append('[');
                for (String each : list)
                {
                    cache.append('"').append(each).append("\",");
                }
                if (cache.isCommaLast())
                {
                    cache.deleteLast();
                }
                cache.append(']');
            }
        });
        System.out.println(strategy.write(new FunctionData11()));
        assertEquals("{\"list\":[\"12\",\"45\"]}", strategy.write(new FunctionData11()));
        System.out.println(strategy.write(new FunctionData12()));
        assertEquals("{\"list\":[\"2015-11-16 00:00:00\",\"2015-11-11 00:00:00\"]}", strategy.write(new FunctionData12()));
    }
    
    @Test
    public void test13()
    {
        WriteStrategy strategy = new WriteStrategy();
        System.out.println(strategy.write(new FunctionData14()));
        assertEquals("{\"array\":[1,2,43],\"array1\":[3,7,9]}", strategy.write(new FunctionData14()));
        strategy = new WriteStrategy();
        strategy.addTypeStrategy(int.class, new WriterAdapter() {
            public void write(int field, StringCache cache, Object entity)
            {
                cache.append(1);
            }
        });
        assertEquals("{\"array\":[1,1,1],\"array1\":[3,7,9]}", strategy.write(new FunctionData14()));
        strategy = new WriteStrategy();
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData14.array", new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append('"').append(1).append('"');
            }
        });
        assertEquals("{\"array\":\"1\",\"array1\":[3,7,9]}", strategy.write(new FunctionData14()));
        strategy = new WriteStrategy();
        strategy.addTypeStrategy(Integer.class, new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                cache.append(1);
            }
        });
        assertEquals("{\"array\":[1,2,43],\"array1\":[1,1,1]}", strategy.write(new FunctionData14()));
    }
    
    @Test
    public void test14()
    {
        FunctionData15 data = new FunctionData15();
        data.setId(1);
        data.setName("linbin");
        data.setUrl("jfire.link");
        data.setIcon("ok");
        // System.out.println(JsonTool.write(data));
        final WriteStrategy strategy = new WriteStrategy();
        strategy.addIgnoreField("com.jfireframework.codejson.test.strategy.FunctionData15.url");
        strategy.addIgnoreField("com.jfireframework.codejson.test.strategy.FunctionData15.icon");
        strategy.addFieldStrategy("com.jfireframework.codejson.test.strategy.FunctionData15.attributes", new WriterAdapter() {
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                FunctionData15 data15 = (FunctionData15) entity;
                Map<String, String> att = (Map<String, String>) field;
                att.put("url", data15.getUrl());
                att.put("icon", data15.getIcon());
                strategy.getWriter(Map.class).write(att, cache, entity, tracker);
            }
        });
        strategy.setUseTracker(true);
        data.setAttributes(new HashMap<String, String>());
        System.out.println(strategy.write(data));
        
    }
}
