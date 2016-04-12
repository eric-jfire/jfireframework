package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.baseutil.tx.TransactionManager;

@Resource
public class TxManager implements TransactionManager
{
    
    @Override
    public void beginTransAction()
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
    
}
