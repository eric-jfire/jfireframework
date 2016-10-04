package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import com.jfireframework.sql.dbstructure.NameStrategy;

@SuppressWarnings("restriction")
public class CalendarField extends AbstractMapField
{
    
    public CalendarField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        Timestamp timestamp = resultSet.getTimestamp(dbColName);
        if (resultSet.wasNull())
        {
            unsafe.putObject(entity, offset, null);
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp.getTime());
            unsafe.putObject(entity, offset, calendar);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        Calendar value = (Calendar) unsafe.getObject(entity, offset);
        if (value == null)
        {
            statement.setDate(index, null);
        }
        else
        {
            statement.setTimestamp(index, new Timestamp(value.getTimeInMillis()));
        }
    }
    
}
