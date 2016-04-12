package com.jfireframework.template;

import java.util.Map;

public interface TemplateOutput
{
    public String output(Map<String, ?> params);
    
    public void print(int i);
    
    public void print(boolean b);
    
    public void print(char c);
    
    public void print(float f);
    
    public void print(double d);
    
    public void print(short s);
    
    public void print(long l);
    
    public void print(byte b);
    
    public void print(Object object);
}
