package com.jfireframework.mvc.vo;

public class Home
{
    private Long   length;
    private Long   width;
    private Person host  = new Person();
    private Desk[] desks = new Desk[2];
    
    public Desk[] getDesks()
    {
        return desks;
    }
    
    public void setDesks(Desk[] desks)
    {
        this.desks = desks;
    }
    
    public void setLength(Long length)
    {
        this.length = length;
    }
    
    public void setWidth(Long width)
    {
        this.width = width;
    }
    
    public long getLength()
    {
        return length;
    }
    
    public void setLength(long length)
    {
        this.length = length;
    }
    
    public long getWidth()
    {
        return width;
    }
    
    public void setWidth(long width)
    {
        this.width = width;
    }
    
    public Person getHost()
    {
        return host;
    }
    
    public void setHost(Person host)
    {
        this.host = host;
    }
    
}
