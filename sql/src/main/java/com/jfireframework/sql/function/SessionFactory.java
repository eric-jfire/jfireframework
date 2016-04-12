package com.jfireframework.sql.function;

public interface SessionFactory
{
    
    /**
     * 获得当前线程内的SqlSession
     * 
     * @return
     */
    public SqlSession getCurrentSession();
    
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
