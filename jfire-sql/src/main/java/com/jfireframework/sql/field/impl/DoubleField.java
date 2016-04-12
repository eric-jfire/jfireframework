package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("restriction")
public class DoubleField extends AbstractMapField
{
    
    public DoubleField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        double value = resultSet.getDouble(dbColName);
        if (resultSet.wasNull() == false)
        {
            unsafe.putDouble(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setDouble(index, unsafe.getDouble(entity, offset));
    }
    
}
