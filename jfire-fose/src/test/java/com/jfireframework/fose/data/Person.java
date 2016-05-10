package com.jfireframework.fose.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person
{
    public String                  name;
    public int                     age  = 56;
    public Person                  leader;
    private List<BaseData>         list = new ArrayList<BaseData>();
    private Map<Integer, BaseData> map  = new HashMap<Integer, BaseData>();
    private int[][]                w    = new int[][] { { 1, 2 }, { 3, 4, 5 } };
    private Date                   date = new Date();
    
    public boolean equals(Object target)
    {
        if (target instanceof Person)
        {
            Person person = (Person) target;
            if (name.equals(person.getName()) && age == person.getAge() && date.equals(person.getDate()) && list.equals(person.getList()) && map.equals(person.getMap()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    public Person()
    {
        this("linbin", 25);
    }
    
    public Person(String name, int age)
    {
        for (int i = 0; i < 10; i++)
        {
            list.add(new BaseData(i));
            map.put(i, new BaseData(i + 30));
        }
        this.name = name;
        this.age = age;
    }
    
    public List<BaseData> getList()
    {
        return list;
    }
    
    public void setList(List<BaseData> list)
    {
        this.list = list;
    }
    
    public Map<Integer, BaseData> getMap()
    {
        return map;
    }
    
    public void setMap(Map<Integer, BaseData> map)
    {
        this.map = map;
    }
    
    public int[][] getW()
    {
        return w;
    }
    
    public void setW(int[][] w)
    {
        this.w = w;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Integer getAge()
    {
        return age;
    }
    
    public Person getLeader()
    {
        return leader;
    }
    
    public void setLeader(Person leader)
    {
        this.leader = leader;
    }
    
    public static void main(String args[]) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        System.out.println(Person.class.getDeclaredField("map").get(new Person()).getClass());
    }
}
