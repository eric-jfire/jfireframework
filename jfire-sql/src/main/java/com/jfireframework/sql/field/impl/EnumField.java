package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.SqlEnumFieldUseInt;
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
        useInt = fieldType.isAnnotationPresent(SqlEnumFieldUseInt.class);
        for (Entry<String, ? extends Enum<?>> each : ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) fieldType).entrySet())
        {
            stringEnumMap.put(each.getKey(), each.getValue());
            intEnumMap.put(each.getValue().ordinal(), each.getValue());
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
        Enum<?> value = (Enum<?>) unsafe.getObject(entity, offset);
        if (useInt)
        {
            if (value != null)
            {
                statement.setInt(index, value.ordinal());
            }
            else
            {
                throw new NullPointerException(StringUtil.format("在进行入参填充时，Enum类型的属性应该必须有值。请检查{}.{}", field.getDeclaringClass().getName(), field.getName()));
            }
        }
        else
        {
            if (value != null)
            {
                statement.setString(index, value.name());
            }
        }
    }
    
}
