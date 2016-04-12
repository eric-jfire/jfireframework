package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

@SuppressWarnings("restriction")
public class TimeField extends AbstractMapField
{
    
    public TimeField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        unsafe.putObject(entity, offset, resultSet.getTime(dbColName));
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        statement.setTime(index, (Time) unsafe.getObject(entity, offset));
    }
    
}
