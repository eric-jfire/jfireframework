package com.jfireframework.context.tx;

public interface TransactionManager
{
    /**
     * 创建当前的session环境。如果已经存在则忽略
     */
    public void buildCurrentSession();
    
    /**
     * 开启事务
     */
    public void beginTransAction();
    
    /**
     * 提交事务,但是并不关闭连接
     */
    public void commit();
    
    /**
     * 事务回滚,但是并不关闭连接
     */
    public void rollback();
    
    /**
     * 关闭当前的session环境
     */
    public void closeCurrentSession();
    
}
