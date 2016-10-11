package com.jfireframework.sql.util.enumhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class EnumCustomIntHandler extends AbstractEnumHandler<Integer>
{
    private final Map<Integer, Enum<?>> map = new HashMap<Integer, Enum<?>>();
    
    public EnumCustomIntHandler(Class<? extends Enum<?>> ckass)
    {
        super(ckass);
        for (Enum<?> each : ReflectUtil.getAllEnumInstances(ckass).values())
        {
            map.put(((EnumIntValue) each).intValue(), each);
        }
    }
    
    @Override
    public Integer getValue(Enum<?> instance)
    {
        return ((EnumIntValue) instance).intValue();
    }
    
    @Override
    public Enum<?> getInstance(ResultSet resultSet) throws SQLException
    {
        int value = resultSet.getInt(1);
        if (resultSet.wasNull() == false)
        {
            return map.get(value);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, int index, Unsafe unsafe, long offfset, Object entity) throws SQLException
    {
        Enum<?> instance = (Enum<?>) unsafe.getObject(entity, offfset);
        if (instance != null)
        {
            statement.setInt(index, ((EnumIntValue) instance).intValue());
        }
    }
    
    @Override
    public void setEntityValue(Unsafe unsafe, long offfset, Object entity, ResultSet resultSet, String dbColName) throws SQLException
    {
        int value = resultSet.getInt(dbColName);
        if (resultSet.wasNull() == false)
        {
            Enum<?> instance = map.get(value);
            unsafe.putObject(entity, offfset, instance);
        }
        else
        {
            unsafe.putObject(entity, offfset, null);
        }
    }
    
}
