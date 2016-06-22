package com.jfireframework.litl;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.litl.resourceloader.FileResLoader;
import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.template.Template;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public class TplTest
{
    public static void main(String[] args) throws FileNotFoundException, SecurityException, NoSuchFieldException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, InterruptedException
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
        TplResLoader loader = new FileResLoader("E:/jfireframework/jfire-litl/");
        TplCenter center = new TplCenter(loader);
        Template template = center.load("/tpl/tmp.tl");
        template.render(data);
        Timewatch timewatch = new Timewatch();
        for (int i = 0; i < 1000; i++)
        {
            
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
}
