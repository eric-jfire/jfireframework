package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.jfireframework.sql.dbstructure.NameStrategy;

@SuppressWarnings("restriction")
public class DateField extends AbstractMapField
{
    
    public DateField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        Timestamp timestamp = resultSet.getTimestamp(dbColName);
        if (timestamp == null)
        {
            unsafe.putObject(entity, offset, null);
        }
        else
        {
            unsafe.putObject(entity, offset, new Date(timestamp.getTime()));
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        java.util.Date value = (java.util.Date) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setDate(index, null);
        }
        else
        {
            statement.setTimestamp(index, new Timestamp(value.getTime()));
        }
    }
    
}
