package com.jfireframework.sql.field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface MapField
{
    /**
     * 从resultset通过名称获取值，并且设置到对象中
     * 
     * @param entity
     * @param resultSet
     * @throws SQLException
     */
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException;
    
    /**
     * 从对象中获取值，按照index设置到statement中
     * 
     * @param statement
     * @param entity
     * @param index
     * @throws SQLException
     */
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException;
    
    /**
     * 获取该属性所对应的数据库字段名称
     * 
     * @return
     */
    public String getColName();
    
    /**
     * 该属性在保存或更新的时候是否会被忽略
     * 
     * @return
     */
    public boolean saveIgnore();
    
    /**
     * 返回该属性的名字
     * 
     * @return
     */
    public String getFieldName();
    
    /**
     * 获取该属性在数据库的对应类型
     * 
     * @return
     */
    public Class<?> getFieldType();
    
    /**
     * 返回该字段代表的数据库的长度。如果是-1代表使用默认值
     * 
     * @return
     */
    public int getDbLength();
    
}
