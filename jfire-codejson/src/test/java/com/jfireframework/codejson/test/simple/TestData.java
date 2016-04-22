package com.jfireframework.codejson.test.simple;

import java.lang.reflect.Type;
import com.jfireframework.codejson.function.JsonReader;
import com.jfireframework.codejson.function.ReadStrategy;

public class TestData
{
    private int    id;
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public static void main(String[] args)
    {
        ReadStrategy readStrategy = new ReadStrategy();
        readStrategy.addFieldStrategy("link.jfire.codejson.test.simple.TestData.name", new JsonReader() {
            
            @Override
            public Object read(Type entityType, Object value)
            {
                System.out.println("daaaaaaas");
                return "林斌";
            }
        });
        String value = "{\"id\":2,\"name\":\"sadasd\"}";
        TestData data = readStrategy.read(TestData.class, value);
        System.out.println(data.getName());
    }
}
