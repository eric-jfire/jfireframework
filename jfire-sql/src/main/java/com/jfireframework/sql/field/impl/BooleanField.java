package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class BooleanField extends AbstractMapField
{
    public BooleanField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        boolean value = resultSet.getBoolean(dbColName);
        if (resultSet.wasNull() == false)
        {
            unsafe.putBoolean(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setBoolean(index, unsafe.getBoolean(entity, offset));
    }
}
