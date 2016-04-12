package com.jfireframework.context.test.function.base.data;

import javax.annotation.Resource;
import com.jfireframework.context.ContextInitFinish;

@Resource
public class House implements ContextInitFinish
{
    private String          name;
    @Resource
    private ImmutablePerson host;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public ImmutablePerson getHost()
    {
        return host;
    }
    
    public void setHost(ImmutablePerson host)
    {
        this.host = host;
    }
    
    @Override
    public void afterContextInit()
    {
        name = "林斌的房子";
    }
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
}
