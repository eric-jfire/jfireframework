package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatTransfer extends AbstractResultsetTransfer<Float>
{
    
    @Override
    protected Float valueOf(ResultSet resultSet) throws Exception
    {
        return Float.valueOf(resultSet.getFloat(1));
    }
    
    public float primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getFloat(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
