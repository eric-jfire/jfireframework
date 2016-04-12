package com.jfireframework.baseutil.tx;

public interface TransactionManager
{
    /**
     * 开启事务
     */
    public void beginTransAction();
    
    /**
     * 提交事务
     */
    public void commit();
    
    /**
     * 事务回滚
     */
    public void rollback();
}
