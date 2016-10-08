package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTransfer extends AbstractResultsetTransfer<Boolean>
{
    
    @Override
    protected Boolean valueOf(ResultSet resultSet) throws Exception
    {
        return Boolean.valueOf(resultSet.getBoolean(1));
    }
    
    public boolean primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getBoolean(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
