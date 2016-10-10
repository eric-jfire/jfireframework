package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.dbstructure.NameStrategy;
import com.jfireframework.sql.util.enumhandler.AbstractEnumHandler;
import com.jfireframework.sql.util.enumhandler.EnumHandler;

public class EnumField extends AbstractMapField
{
    private final EnumHandler<?> enumHandler;
    
    @SuppressWarnings("unchecked")
    public EnumField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
        Class<?> fieldType = field.getType();
        try
        {
            enumHandler = AbstractEnumHandler.getEnumBoundHandler((Class<? extends Enum<?>>) fieldType).getConstructor(Class.class).newInstance(fieldType);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        enumHandler.setEntityValue(unsafe, offset, entity, resultSet, dbColName);
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        enumHandler.setStatementValue(statement, index, unsafe, offset, entity);
    }
    
}
