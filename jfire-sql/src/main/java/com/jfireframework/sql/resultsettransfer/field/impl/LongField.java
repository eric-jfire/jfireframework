package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;

public class LongField extends AbstractMapField
{
    
    public LongField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        long value = resultSet.getLong(dbColName);
        if (resultSet.wasNull() == false)
        {
            unsafe.putLong(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setLong(index, unsafe.getLong(entity, offset));
    }
    
}
