package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.context.tx.RessourceManager;

@Resource
public class AcManager implements RessourceManager
{
    
    @Override
    public void close()
    {
        System.out.println("关闭资源");
    }
    
    @Override
    public void open()
    {
        System.out.println("打开资源");
    }
    
}
