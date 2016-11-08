package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerTransfer extends AbstractResultsetTransfer<Integer>
{
    
    @Override
    protected Integer valueOf(ResultSet resultSet, String sql) throws SQLException
    {
        return Integer.valueOf(resultSet.getInt(1));
    }
    
}
