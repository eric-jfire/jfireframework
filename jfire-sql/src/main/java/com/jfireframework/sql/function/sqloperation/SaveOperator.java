package com.jfireframework.sql.function.sqloperation;

import java.util.List;

public interface SaveOperator
{
    /**
     * 将一个对象保存或者更新到数据库。如果对象的id属性有值，就是更新操作，如果没有值就是插入操作
     * 
     * @param <T>
     *            
     * @param entity
     * @return
     */
    public <T> void save(T entity);
    
    /**
     * 批量保存一个list中的数据
     * 
     * @param <T>
     * @param entitys
     */
    public <T> void batchInsert(List<T> entitys);
    
    /**
     * 将一个对象以插入的形式保存到数据库
     * 
     * @param entity
     */
    public void insert(Object entity);
}
