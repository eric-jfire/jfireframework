package com.jfireframework.sql.util.enumhandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class EnumStringHandler extends AbstractEnumHandler<String>
{
    private final Map<String, ? extends Enum<?>> map;
    
    public EnumStringHandler(Class<? extends Enum<?>> ckass)
    {
        super(ckass);
        map = ReflectUtil.getAllEnumInstances(ckass);
    }
    
    @Override
    public String getValue(Enum<?> instance)
    {
        if (instance == null)
        {
            return null;
        }
        return instance.name();
    }
    
    @Override
    public void setEntityValue(Unsafe unsafe, long offfset, Object entity, ResultSet resultSet, String dbColName) throws SQLException
    {
        String value = resultSet.getString(dbColName);
        Enum<?> instance = map.get(value);
        unsafe.putObject(entity, offfset, instance);
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, int index, Unsafe unsafe, long offfset, Object entity) throws SQLException
    {
        Enum<?> value = (Enum<?>) unsafe.getObject(entity, offfset);
        statement.setString(index, value.name());
    }
    
    @Override
    public Enum<?> getInstance(ResultSet resultSet) throws SQLException
    {
        String value = resultSet.getString(1);
        if (value != null)
        {
            return map.get(value);
        }
        else
        {
            return null;
        }
    }
    
}
