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
    public <T> boolean delete(T entity);
    
    /**
     * 通过id字符串（内容格式为1,2,3,4）对数据行进行批量删除
     * 
     * @param <T>
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public <T> int deleteByIds(Class<T> entityClass, String ids);
    
    /**
     * 通过id数组，对数据行进行批量删除
     * 
     * @param <T>
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public <T> int deleteByIds(Class<T> entityClass, int[] ids);
    
    /**
     * 使用给定的属性对一个映射到类的数据库行进行更新
     * 
     * @param <T>
     * 
     * @param entity
     * @param fieldNames
     * @return
     */
    public <T> int selectUpdate(T entity, String fieldNames);
    
}
