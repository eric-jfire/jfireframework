package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class WBooleanField extends AbstractMapField
{
    
    public WBooleanField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        boolean value = resultSet.getBoolean(dbColName);
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
        Boolean value = (Boolean) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setObject(index, null);
        }
        else
        {
            statement.setBoolean(index, value);
        }
    }
    
}
