package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;

public class FloatField extends AbstractMapField
{
    
    public FloatField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        float value = resultSet.getFloat(dbColName);
        if (resultSet.wasNull() == false)
        {
            unsafe.putFloat(entity, offset, value);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setFloat(index, unsafe.getFloat(entity, offset));
    }
    
}
