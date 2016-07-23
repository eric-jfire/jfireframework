package com.jfireframework.sql.function;

import java.sql.Connection;
import com.jfireframework.sql.function.sqloperation.GetOperator;
import com.jfireframework.sql.function.sqloperation.SaveOperator;
import com.jfireframework.sql.function.sqloperation.SqlOperator;
import com.jfireframework.sql.function.sqloperation.UpdateOperator;

/**
 * 代表一个connection链接，提供各种dao操作入口
 * 
 * @author eric
 * 
 */
public interface SqlSession extends GetOperator, SaveOperator, UpdateOperator, SqlOperator
{
    
    /**
     * 关闭session，释放数据库链接
     */
    public void close();
    
    /**
     * 启动事务,将该数据库链接设置为非自动提交模式
     */
    public void beginTransAction();
    
    /**
     * 依据事务传播策略进行事务提交请求操作（在单一事务传播情况下，内嵌事务的提交只会消耗提交数，不会真的执行提交操作）
     */
    public void commit();
    
    /**
     * 提交事务到数据库，但不改变当前数据库链接的提交模式
     */
    public void flush();
    
    /**
     * 事务回滚
     */
    public void rollback();
    
    /**
     * 获取当前session使用的数据库链接
     * 
     * @return
     */
    public Connection getConnection();
    
}
