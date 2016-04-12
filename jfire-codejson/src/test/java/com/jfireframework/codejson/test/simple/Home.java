package com.jfireframework.codejson.test.simple;

public class Home
{
    private float  wdith  = 12.36f;
    private float  length = 50.26f;
    private Person person = new Person();
    
    public float getWdith()
    {
        return wdith;
    }
    
    public void setWdith(float wdith)
    {
        this.wdith = wdith;
    }
    
    public float getLength()
    {
        return length;
    }
    
    public void setLength(float length)
    {
        this.length = length;
    }
    
    public Person getPerson()
    {
        return person;
    }
    
    public void setPerson(Person person)
    {
        this.person = person;
    }
    
}
