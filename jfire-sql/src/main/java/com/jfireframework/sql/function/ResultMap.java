package com.jfireframework.sql.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultMap<T>
{
    /**
     * 将resultset中的值转换到bean中,从resultset中取值使用名称，名称是属性所对应的数据库列名
     * 
     * @param fieldNames 需要进行设置的属性名称，如果为空或者为null，则所有的属性均需要设置
     * @param resultSet 查询的结果集
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<T> toBean(ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException;
    
    /**
     * 使用查询结果返回一个唯一的对象
     * 
     * @param resultSet
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public T singleResultToBean(ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException;
    
}
