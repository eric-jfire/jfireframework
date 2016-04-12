package com.jfireframework.sql.function.sqloperation;

import java.util.List;

public interface SqlOperator
{
    /**
     * 使用sql语句进行查询，返回的数据内容的类型格式和resultTypes一致。每一行数据组装成一个Object[]放入list中
     * 
     * @param resultType
     * @param sql
     * @param params
     * @return
     */
    public List<Object[]> listQuery(Class<?>[] resultTypes, String sql, Object... params);
    
    /**
     * 使用sql语句进行查询，返回的内容是多行，使用自定义对象进行映射组装
     * 
     * @param resultTypes
     * @param sql
     * @param params
     * @return
     */
    public <T> List<T> listQuery(Class<T> resultTypes, String sql, Object... params);
    
    /**
     * 使用sql语句进行查询，返回的内容是多行单列，数据内容的类型是基本类型
     * 
     * @param resultTypes
     * @param sql
     * @param params
     * @return
     */
    public <T> List<T> baseListQuery(Class<T> resultType, String sql, Object... params);
    
    /**
     * 使用sql语句进行查询，要求返回的只能是单行单列，type类型为基本类型或者基本类型的包装类
     * 
     * @param resultType
     * @param sql
     * @param params
     * @return
     */
    public <T> T baseQuery(Class<T> resultType, String sql, Object... params);
    
    /**
     * 使用sql语句进行查询，要求返回的只能单行，type类型为自定义的对象类型，将数据映射到对象的属性之中
     * 
     * @param resultTYpe
     * @param sql
     * @param params
     * @return
     */
    public <T> T query(Class<T> resultType, String sql, Object... params);
    
    /**
     * 根据给定的sql语句以及参数，进行更新类型的sql语句操作
     * 
     * @param sql
     * @param params 需要更新的参数，参数顺序需要与sql语句中的一致
     * @return 返回操作的数据库行数
     */
    public int update(String sql, Object... params);
    
    /**
     * 根据给定的sql，执行批量插入。一次迭代为一行更新的数据
     * 
     * @param sql 给定的格式化sql
     * @param iterator 迭代器，每一次迭代的数据为一行的数据
     */
    public int[] batchUpdate(String sql, List<Object[]> iterator);
}
