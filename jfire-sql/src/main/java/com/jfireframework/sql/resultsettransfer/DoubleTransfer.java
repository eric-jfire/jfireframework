package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleTransfer extends AbstractResultsetTransfer<Double>
{
    
    @Override
    protected Double valueOf(ResultSet resultSet) throws Exception
    {
        return Double.valueOf(resultSet.getDouble(1));
    }
    
    public double primitiveValue(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            return resultSet.getDouble(1);
        }
        else
        {
            throw new NullPointerException("sql操作没有返回结果。请确认是否应该使用包装类");
        }
    }
}
