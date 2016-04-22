package com.jfireframework.codejson.test.simple;

import org.junit.Test;
import com.jfireframework.codejson.function.WriteStrategy;

public class Room2
{
    private String        name;
    private Person2[][][] person2s;
    
    @Test
    public void test()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        Room2 room2 = new Room2();
        Person2 person2 = new Person2();
        person2.setAge(1);
        Person2[] array = new Person2[2];
        array[0] = person2;
        person2 = new Person2();
        person2.setAge(10);
        array[1] = person2;
        Person2[][][] array1 = new Person2[][][] { { array, array }, { array, array } };
        room2.setPerson2s(array1);
        System.out.println(strategy.write(room2));
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Person2[][][] getPerson2s()
    {
        return person2s;
    }
    
    public void setPerson2s(Person2[][][] person2s)
    {
        this.person2s = person2s;
    }
    
}
