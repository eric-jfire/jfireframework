package com.jfireframework.litl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.junit.Before;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.litl.resourceloader.FileResLoader;

public class SpeedTest
{
    private Map<String, Object> data;
    private int                 count = 100000;
    
    @Before
    public void before()
    {
        data = new HashMap<String, Object>();
        data.put("myname", "小静");
        List<Person> persons = new LinkedList<Person>();
        Person person = new Person();
        person.setName("林斌");
        // data.put("person", person);
        persons.add(person);
        person = new Person();
        person.setName("小静");
        persons.add(person);
        data.put("persons", persons.toArray());
        data.put("title", "题目");
        data.put("today", new Date());
        data.put("stringparam", "12");
        data.put("iparam", 12);
        data.put("dparam", 56.36);
    }
    
    @Test
    public void test() throws IOException
    {
        FileResourceLoader fileResourceLoader = new FileResourceLoader("E:/jfireframework/jfire-litl/tpl/");
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(fileResourceLoader, cfg);
        Template template = gt.getTemplate("speedforbeetl.tl");
        template.binding(data);
        template.render();
        Timewatch timewatch = new Timewatch();
        for (int i = 0; i < count; i++)
        {
            template = gt.getTemplate("speedforbeetl.tl");
            template.binding(data);
            template.render();
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
        FileResLoader loader = new FileResLoader(new File("E:/jfireframework/jfire-litl/tpl/"));
        TplCenter center = new TplCenter(loader);
        com.jfireframework.litl.template.Template liTemplate = center.load("speedforlitl.tl");
        liTemplate.render(data);
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            center.load("speedforlitl.tl").render(data);
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            template = gt.getTemplate("speedforbeetl.tl");
            template.binding(data);
            template.render();
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            center.load("speedforlitl.tl").render(data);
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
}
