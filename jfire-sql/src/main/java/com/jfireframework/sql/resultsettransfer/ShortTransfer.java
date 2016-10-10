package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortTransfer extends AbstractResultsetTransfer<Short>
{
    
    public ShortTransfer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    protected Short valueOf(ResultSet resultSet) throws Exception
    {
        return Short.valueOf(resultSet.getShort(1));
    }
    
    public short primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getShort(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
