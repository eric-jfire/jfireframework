package com.jfireframework.sql.function.sqloperation;

import com.jfireframework.sql.function.LockMode;

public interface GetOperator
{
    
    /**
     * 根据主键获取一条记录，并且使用该记录创造一个对象
     * 
     * @param entityClass 代表数据库表的类对象
     * @param pk 主键
     * @return 代表该行记录的对象实例
     */
    public <T> T get(Class<T> entityClass, Object pk);
    
    /**
     * 根据findby获取一条记录，并且使用该记录创造一个对象
     * 
     * @param entityClass 代表数据库表的类对象
     * @param pk 主键
     * @return 代表该行记录的对象实例
     */
    public <T> T findBy(Class<T> entityClass, Object param);
    
    /**
     * 根据主键获取一条记录，并且使用该记录创造一个对象.获取的时候使用给定的锁定模式
     * 
     * @param entityClass 代表数据库表的类对象
     * @param pk 主键
     * @return 代表该行记录的对象实例
     */
    public <T> T get(Class<T> entityClass, Object pk, LockMode mode);
}
