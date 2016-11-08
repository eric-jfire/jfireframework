package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;

public class SqlDateField extends AbstractMapField
{
    public SqlDateField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        Date date = resultSet.getDate(dbColName);
        unsafe.putObject(entity, offset, date);
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        Date value = (Date) unsafe.getObject(entity, offset);
        statement.setDate(index, value);
    }
}
