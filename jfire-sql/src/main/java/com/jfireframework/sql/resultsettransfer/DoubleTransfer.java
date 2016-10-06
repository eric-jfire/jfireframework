package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class DoubleTransfer extends AbstractResultsetTransfer<Double>
{
    
    public DoubleTransfer(Class<Double> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    protected Double valueOf(ResultSet resultSet) throws Exception
    {
        return Double.valueOf(resultSet.getDouble(1));
    }
    
}
