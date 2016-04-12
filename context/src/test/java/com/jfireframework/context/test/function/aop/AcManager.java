package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.baseutil.tx.AutoCloseManager;

@Resource
public class AcManager implements AutoCloseManager
{
    
    @Override
    public void close()
    {
        System.out.println("关闭资源");
    }
    
}
