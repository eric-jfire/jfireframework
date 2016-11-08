package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;

public class WFloatField extends AbstractMapField
{
    
    public WFloatField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        float value = resultSet.getFloat(dbColName);
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
        Float value = (Float) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setObject(index, null);
        }
        else
        {
            statement.setFloat(index, value);
        }
    }
    
}
