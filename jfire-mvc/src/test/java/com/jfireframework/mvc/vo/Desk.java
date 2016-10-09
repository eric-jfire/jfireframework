package com.jfireframework.mvc.vo;

public class Desk
{
    private String name;
    private int    width;
    private length l;
    
    public length getL()
    {
        return l;
    }
    
    public void setL(length l)
    {
        this.l = l;
    }
    
    public static enum length
    {
        LONG, SHORT;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
}
