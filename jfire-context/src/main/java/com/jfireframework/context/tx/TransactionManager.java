package com.jfireframework.context.tx;

public interface TransactionManager
{
    /**
     * 创建当前的session环境。如果已经存在则忽略
     */
    public void buildCurrentSession();
    
    /**
     * 开启事务,数字为该事务的隔离级别。如果为-1，则意味着使用不修改，使用该连接自身的隔离级别
     */
    public void beginTransAction(int isolate);
    
    /**
     * 提交事务,但是并不关闭连接
     */
    public void commit();
    
    /**
     * 事务回滚,但是并不关闭连接
     */
    public void rollback(Throwable e);
    
    /**
     * 关闭当前的session环境
     */
    public void closeCurrentSession();
    
}
