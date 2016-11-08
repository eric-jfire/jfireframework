package com.jfireframework.sql.function;

public interface SessionFactory
{
    
    /**
     * 获取对应的类型的Dao对象
     * 
     * @param <T>
     * @param ckass
     * @return
     */
    public <T> Dao<T> getDao(Class<T> ckass);
    
    /**
     * 根据给定的接口，返回符合sqlorm规范的接口实现
     * 
     * @param entityClass
     * @return
     */
    public <T> T getMapper(Class<T> entityClass);
    
    /**
     * 获得当前线程内的SqlSession
     * 
     * @return
     */
    public SqlSession getCurrentSession();
    
    /**
     * 调用getCurrentSession获得session，如果存在就返回。如果没有值，则使用openSession创建一个并且存储于线程内并返回
     * 
     * @return
     */
    public SqlSession getOrCreateCurrentSession();
    
    /**
     * 重新打开一个SqlSession
     * 
     * @return
     */
    public SqlSession openSession();
    
    /**
     * 移除当前线程的SqlSession
     * 
     */
    public void removeCurrentSession();
    
    /**
     * 将一个session设置到当前的线程中
     * 
     * @param session
     */
    public void setCurrentSession(SqlSession session);
    
}
