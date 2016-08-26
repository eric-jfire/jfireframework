package com.jfireframework.mvc;

import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.newbinder.TreeValueNode;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;
import com.jfireframework.mvc.vo.Desk;

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
        ObjectDataBinder binder = new ObjectDataBinder(Desk.class, "");
        Timewatch timewatch = new Timewatch();
        TreeValueNode paramTree = new TreeValueNode();
        paramTree.put("name", "hello");
        paramTree.put("width", "20");
        Desk desk = (Desk) binder.binder(null, paramTree, null);
        timewatch.end();
        System.out.println(timewatch.getTotal());
        System.out.println(JsonTool.write(desk));
    }
}
