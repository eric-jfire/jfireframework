package com.jfireframework.litl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public class TplTest
{
    public static void main(String[] args) throws FileNotFoundException, SecurityException, NoSuchFieldException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
    {
        // LineReader lineReader = new LineReader(new File("tmp.tl"),
        // Charset.forName("utf8"));
        // TreeMap<Integer, String> context = new TreeMap<Integer, String>();
        // String value = null;
        // int line = 1;
        // while ((value = lineReader.readLine()) != null)
        // {
        // context.put(line, value);
        // line += 1;
        // }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("myname", "小静");
        List<Person> persons = new LinkedList<Person>();
        Person person = new Person();
        person.setName("林斌");
        persons.add(person);
        person = new Person();
        person.setName("小静");
        persons.add(person);
        data.put("persons", persons);
        data.put("title", "题目");
        data.put("today", new Date());
        TplRender render = new TplCenter(new File("E:/jfireframekwork/jfire-template/")).get("tmp.tl", data);
        System.out.println(render.render(data));
    }
}
