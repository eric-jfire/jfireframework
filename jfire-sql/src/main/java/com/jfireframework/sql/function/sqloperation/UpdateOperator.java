package com.jfireframework.sql.function.sqloperation;

public interface UpdateOperator
{
    /**
     * 删除对象所对应的表的一条记录
     * 
     * @param <T>
     * 
     * @param entityClass 代表数据库表的类对象
     * @param pk 主键
     * @return
     */
    public <T> int delete(T entity);
    
}
