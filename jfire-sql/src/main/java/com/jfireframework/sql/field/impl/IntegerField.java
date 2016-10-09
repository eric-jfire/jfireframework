package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class IntegerField extends AbstractMapField
{
    
    public IntegerField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        int value = resultSet.getInt(dbColName);
        if (resultSet.wasNull())
        {
            unsafe.putObject(entity, offset, null);
        }
        else
        {
            unsafe.putObject(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        Integer value = (Integer) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setObject(index, null);
        }
        else
        {
            statement.setInt(index, value);
        }
    }
    
}
