package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.context.tx.TransactionManager;

@Resource
public class TxManager implements TransactionManager
{
    
    @Override
    public void beginTransAction(int isolate)
    {
        System.out.println("事务开启");
    }
    
    @Override
    public void commit()
    {
        System.out.println("事务结束");
        
    }
    
    @Override
    public void rollback()
    {
        System.out.println("事务回滚");
    }
    
    @Override
    public void buildCurrentSession()
    {
        System.out.println("打开session");
    }
    
    @Override
    public void closeCurrentSession()
    {
        System.out.println("关闭session");
    }
    
}
