package com.jfireframework.baseutil.data;

public class Home
{
    private Person   host;
    private int      length;
    private Person[] liveins = new Person[2];
    private int      width;
                     
    public Person getHost()
    {
        return host;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public Person[] getLiveins()
    {
        return liveins;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setHost(Person host)
    {
        this.host = host;
    }
    
    public void setLength(int length)
    {
        this.length = length;
    }
    
    public void setLiveins(Person[] liveins)
    {
        this.liveins = liveins;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
}
