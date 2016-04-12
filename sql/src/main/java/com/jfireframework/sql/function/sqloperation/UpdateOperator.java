package com.jfireframework.sql.function.sqloperation;

public interface UpdateOperator
{
    /**
     * 删除对象所对应的表的一条记录
     * 
     * @param entityClass 代表数据库表的类对象
     * @param pk 主键
     * @return
     */
    public boolean delete(Object entity);
    
    /**
     * 通过id字符串（内容格式为1,2,3,4）对数据行进行批量删除
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public int deleteByIds(Class<?> entityClass, String ids);
    
    /**
     * 通过id数组，对数据行进行批量删除
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public int deleteByIds(Class<?> entityClass, int[] ids);
    
    /**
     * 使用给定的属性对一个映射到类的数据库行进行更新
     * 
     * @param entity
     * @param fieldNames
     * @return
     */
    public int selectUpdate(Object entity, String fieldNames);
    
}
