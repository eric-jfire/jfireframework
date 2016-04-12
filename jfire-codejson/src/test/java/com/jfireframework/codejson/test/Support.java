package com.jfireframework.codejson.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.codejson.methodinfo.MethodInfoBuilder;

public class Support
{
    protected Logger logger = ConsoleLogFactory.getLogger();
    protected Data   data;
    
    public Support()
    {
        new MethodInfoBuilder();
        data = new Data();
        data.setA(12);
        data.setB(2.36f);
        data.setC(5.6987);
        data.setD(121212121212l);
        data.setE('f');
        data.setF(true);
        data.setG((short) 5.689);
        data.setH((byte) 3);
        data.setA1(1);
        data.setB1(2.34f);
        data.setC1(2323.34234234);
        data.setD1(11231231231313133l);
        data.setE1("sdasdasd");
        data.setF1(true);
        data.setG1((short) 2);
        data.setH1((byte) 12);
        NestData nestData = new NestData();
        nestData.setName("dsadas");
        data.setNestData(nestData);
        ArrayList<String> list = new ArrayList<>();
        list.add("husdasdad");
        list.add("siudsan");
        data.setList(list);
        ArrayList<NestData> nestDatas = new ArrayList<>();
        nestData = new NestData();
        nestData.setName("sdasda");
        nestData.setAge(13);
        nestDatas.add(nestData);
        nestData = new NestData();
        nestData.setName("dasdas");
        nestData.setAge(20);
        nestDatas.add(nestData);
        data.setDatas(nestDatas);
        data.setNolist(new ArrayList<String>());
        HashMap<String, String> map = new HashMap<>();
        map.put("恁大", "dasdasd");
        map.put("dsada", "你好");
        data.setMap(map);
        data.setArray2(new int[][] { { 1, 2, 3, 4 }, { 10, 12 } });
        data.setStrs(new String[] { "231231", "sdadsasdasd" });
        data.setArray1(new int[] { 1, 2, 3, 4, 5, 65 });
        data.setChars(new char[] { 'a', 'b' });
        data.setArray3(new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
        data.setArray4(new Integer[][] { { 1, 2, 3, 4, 5, 56 }, { 10, 11, 12, 14 } });
        NestData[] nestDatas2 = new NestData[2];
        NestData tmp = new NestData();
        tmp.setAge(12);
        tmp.setName("das");
        nestDatas2[0] = tmp;
        tmp = new NestData();
        tmp.setAge(1222);
        tmp.setName("daasdadasd");
        nestDatas2[1] = tmp;
        data.setNestDatas(nestDatas2);
        HashMap<Date, NestData> map2 = new HashMap<>();
        map2.put(new Date(), tmp);
        @SuppressWarnings("unchecked")
        ArrayList<String>[] lists = new ArrayList[] { new ArrayList<>(), new ArrayList<>() };
        lists[0].add("dasdasda");
        lists[0].add("dasdasdasdasdasd");
        lists[1].add("1212121dasdasdasdasdasd");
        lists[1].add("dasdasd1212121212asdasdasd");
        data.setLists(lists);
        data.setData(nestDatas2);
    }
    
}
