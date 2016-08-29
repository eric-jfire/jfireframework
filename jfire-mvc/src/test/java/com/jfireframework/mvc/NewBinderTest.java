package com.jfireframework.mvc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.binder.impl.ObjectDataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;
import com.jfireframework.mvc.vo.Desk;
import com.jfireframework.mvc.vo.Home;
import com.jfireframework.mvc.vo.Person;

public class NewBinderTest
{
    
    @Test
    public void test()
    {
        TreeValueNode paramTree = new TreeValueNode();
        paramTree.put("foo[bar][numbers][]", "1");
        paramTree.put("foo[bar][numbers][]", "2");
        paramTree.put("foo[bar][numbers][]", "3");
        paramTree.put("foo[id]", "foo01");
        paramTree.put("foo[bar][id]", "bar01");
        paramTree.put("foo[barsMap][bar02][id]", "19");
        paramTree.put("foo[barsMap][bar02][numbers][]", "19");
        paramTree.put("foo[barsMap][bar03][id]", "foo01");
        paramTree.put("foo[barsMap][bar03][numbers][]", "foo01");
        System.out.println(JsonTool.write(paramTree));
    }
    
    @Test
    public void test2()
    {
        ObjectDataBinder binder = new ObjectDataBinder(Desk.class, "", null);
        TreeValueNode paramTree = new TreeValueNode();
        paramTree.put("name", "hello");
        paramTree.put("width", "20");
        Desk desk = (Desk) binder.bind(null, paramTree, null);
        assertEquals("hello", desk.getName());
        assertEquals(20, desk.getWidth());
        binder = new ObjectDataBinder(Desk.class, "desk", null);
        paramTree.clear();
        paramTree.put("desk[name]", "hello");
        paramTree.put("desk[width]", "20");
        desk = (Desk) binder.bind(null, paramTree, null);
        assertEquals("hello", desk.getName());
        assertEquals(20, desk.getWidth());
    }
    
    @Test
    public void test3()
    {
        ObjectDataBinder binder = new ObjectDataBinder(Person.class, "", null);
        TreeValueNode node = new TreeValueNode();
        node.put("age", "15");
        node.put("name", "test");
        node.put("weight", "15.36");
        node.put("ids[0]", "1");
        node.put("ids[1]", "2");
        node.put("ids[2]", "3");
        Person person = (Person) binder.bind(null, node, null);
        assertEquals(15, person.getAge());
        assertEquals("test", person.getName());
        assertEquals(15.36, person.getWeight(), 0.0001);
        Integer[] ids = person.getIds();
        assertArrayEquals(new Integer[] { 1, 2, 3 }, ids);
    }
    
    @Test
    public void test4()
    {
        TreeValueNode map = new TreeValueNode();
        map.put("host[name]", "林斌");
        map.put("host[age]", "25");
        map.put("host[weight]", "75.26");
        map.put("host[ids][0]", "1");
        map.put("host[ids][1]", "10");
        map.put("host[ids][3]", "100");
        map.put("length", "100");
        map.put("width", "50");
        map.put("desks[0][name]", "desk1");
        map.put("desks[0][width]", "11");
        map.put("desks[1][name]", "desk2");
        map.put("desks[1][width]", "12");
        ObjectDataBinder binder = new ObjectDataBinder(Home.class, "", null);
        Home home = (Home) binder.bind(null, map, null);
        assertEquals(home.getHost().getName(), "林斌");
        assertEquals(home.getHost().getAge(), 25);
        assertEquals(home.getHost().getWeight(), 75.26, 0.001);
        Integer[] ids = home.getHost().getIds();
        assertEquals(ids[0].intValue(), 1);
        assertEquals(ids[1].intValue(), 10);
        assertEquals(ids[3].intValue(), 100);
        Desk[] desks = home.getDesks();
        assertEquals("desk1", desks[0].getName());
        assertEquals("desk2", desks[1].getName());
        assertEquals(11, desks[0].getWidth());
        assertEquals(12, desks[1].getWidth());
    }
}
