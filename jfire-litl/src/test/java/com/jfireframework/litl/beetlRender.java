package com.jfireframework.litl;

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

public class beetlRender
{
    public static void main(String[] args) throws IOException
    {
        Map<String, Object> data = new HashMap<String, Object>();
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
        FileResourceLoader fileResourceLoader = new FileResourceLoader("E:/jfireframework/jfire-litl/tpl/");
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(fileResourceLoader, cfg);
        Template template = gt.getTemplate("speedforbeetl.tl");
        template.binding(data);
        System.out.println(template.render());;
    }
}
