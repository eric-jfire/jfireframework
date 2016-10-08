package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerTransfer extends AbstractResultsetTransfer<Integer>
{
    
    @Override
    protected Integer valueOf(ResultSet resultSet) throws SQLException
    {
        return Integer.valueOf(resultSet.getInt(1));
    }
    
    public int primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getInt(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
