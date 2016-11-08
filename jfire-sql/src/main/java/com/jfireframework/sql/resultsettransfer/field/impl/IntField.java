package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;

public class IntField extends AbstractMapField
{
    
    public IntField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        int value = resultSet.getInt(dbColName);
        if (resultSet.wasNull() == false)
        {
            unsafe.putInt(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setInt(index, unsafe.getInt(entity, offset));
    }
    
}
