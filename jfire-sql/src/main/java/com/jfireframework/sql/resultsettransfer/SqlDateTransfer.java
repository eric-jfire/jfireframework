package com.jfireframework.sql.resultsettransfer;

import java.sql.Date;
import java.sql.ResultSet;

public class SqlDateTransfer extends AbstractResultsetTransfer<java.sql.Date>
{
    
    public SqlDateTransfer(Class<?> type)
    {
        super(type);
    }

    @Override
    protected Date valueOf(ResultSet resultSet) throws Exception
    {
        return resultSet.getDate(1);
    }
    
}
