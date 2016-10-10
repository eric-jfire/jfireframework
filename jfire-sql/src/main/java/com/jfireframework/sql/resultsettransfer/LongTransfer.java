package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongTransfer extends AbstractResultsetTransfer<Long>
{
    public LongTransfer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    protected Long valueOf(ResultSet resultSet) throws Exception
    {
        return Long.valueOf(resultSet.getLong(1));
    }
    
    public long primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getLong(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
