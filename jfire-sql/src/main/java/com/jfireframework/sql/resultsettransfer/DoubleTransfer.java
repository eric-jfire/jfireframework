package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class DoubleTransfer extends AbstractResultsetTransfer<Double>
{
    
    @Override
    protected Double valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return Double.valueOf(resultSet.getDouble(1));
    }
    
}
