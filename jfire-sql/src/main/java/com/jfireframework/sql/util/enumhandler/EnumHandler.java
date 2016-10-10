package com.jfireframework.sql.util.enumhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sun.misc.Unsafe;

public interface EnumHandler<T>
{
    public T getValue(Enum<?> instance);
    
    public Enum<?> getInstance(ResultSet resultSet) throws SQLException;
    
    public void setStatementValue(PreparedStatement statement, int index, Unsafe unsafe, long offfset, Object entity) throws SQLException;
    
    public void setEntityValue(Unsafe unsafe, long offfset, Object entity, ResultSet resultSet, String dbColName) throws SQLException;
}
