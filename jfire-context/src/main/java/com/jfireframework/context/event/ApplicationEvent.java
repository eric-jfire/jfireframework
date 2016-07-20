package com.jfireframework.context.event;

public final class ApplicationEvent
{
    private Object  data;
    private Enum<?> type;
    
    public void setData(Object data)
    {
        this.data = data;
    }
    
    public void setType(Enum<?> type)
    {
        this.type = type;
    }
    
    public Object getData()
    {
        return data;
    }
    
    public Enum<?> getType()
    {
        return type;
    }
    
}
