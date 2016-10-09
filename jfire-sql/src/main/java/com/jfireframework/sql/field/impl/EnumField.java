package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.annotation.EnumUseInt;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class EnumField extends AbstractMapField
{
    private final boolean               useInt;
    private final Map<String, Enum<?>>  stringEnumMap = new HashMap<String, Enum<?>>();
    private final Map<Integer, Enum<?>> intEnumMap    = new HashMap<Integer, Enum<?>>();
    
    @SuppressWarnings("unchecked")
    public EnumField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
        Class<?> fieldType = field.getType();
        useInt = field.isAnnotationPresent(EnumUseInt.class);
        try
        {
            Method method = Class.class.getDeclaredMethod("enumConstantDirectory");
            method.setAccessible(true);
            Map<String, Enum<?>> map = (Map<String, Enum<?>>) method.invoke(fieldType);
            for (Entry<String, Enum<?>> each : map.entrySet())
            {
                stringEnumMap.put(each.getKey(), each.getValue());
                intEnumMap.put(each.getValue().ordinal(), each.getValue());
            }
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        if (useInt)
        {
            int value = resultSet.getInt(dbColName);
            if (resultSet.wasNull() == false)
            {
                unsafe.putObject(entity, offset, intEnumMap.get(value));
            }
        }
        else
        {
            String value = resultSet.getString(dbColName);
            if (value != null)
            {
                unsafe.putObject(entity, offset, stringEnumMap.get(value));
            }
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
}
