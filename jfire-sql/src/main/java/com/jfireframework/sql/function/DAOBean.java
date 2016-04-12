package com.jfireframework.sql.function;

import java.sql.Connection;
import java.util.List;

public interface DAOBean
{
    /**
     * 将对象信息保存到数据库中。如果对象id值为null，进行插入操作，否则进行更新操作
     * 
     * @param <T>
     *            
     * @param entity
     * @param connection
     * @return
     */
    public <T> void save(T entity, Connection connection);
    
    /**
     * 批量将一个list中的数据保存到数据库中
     * 
     * @param <T>
     *            
     * @param entitys
     * @param connection
     */
    public <T> void batchInsert(List<T> entitys, Connection connection);
    
    /**
     * 将一个对象以插入数据的形式保存到数据库
     * 
     * @param <T>
     *            
     * @param entity
     * @param connection
     */
    public <T> void insert(T entity, Connection connection);
    
    /**
     * 将对象entity所代表的数据库行删除.
     * entity其他参数并不重要,只要id参数有存在即可.删除是根据id参数进行删除的
     * 
     * @param pk
     * @param connection
     * @return
     */
    public boolean delete(Object entity, Connection connection);
    
    /**
     * 在数据库该表中，使用主键查询并且返回对象
     * 
     * @param <T>
     *            
     * @param pk
     * @param connection
     * @return
     */
    public <T> T getById(Object pk, Connection connection);
    
    /**
     * 在数据表该表中，使用主键查询并且返回对象，但是使用某一个锁定模式
     * 
     * @param pk
     * @param connection
     * @param mode
     * @return
     */
    public <T> T getById(Object pk, Connection connection, LockMode mode);
    
    /**
     * 根据主键进行查询对应的字段并且组装成对象返回
     * 
     * @param pk
     * @param connection
     * @param fieldNames
     * @return
     */
    public <T> T getById(Object pk, Connection connection, String fieldNames);
    
    /**
     * 以主键作为条件更新，更新的字段由属性名称fieldNames确定
     * 
     * @param entity
     * @param connection
     * @param fieldNames
     */
    public <T> int update(T entity, Connection connection, String fieldNames);
    
    /**
     * 通过id字符串（内容格式为1,2,3,4）对数据行进行批量删除
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public int deleteByIds(String ids, Connection connection);
    
    /**
     * 通过id数组，对数据行进行批量删除
     * 
     * @param entityClass
     * @param ids
     * @return
     */
    public int deleteByIds(int[] ids, Connection connection);
    
}
