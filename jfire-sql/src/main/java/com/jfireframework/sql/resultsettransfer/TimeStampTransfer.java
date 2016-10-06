package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.Timestamp;

public class TimeStampTransfer extends AbstractResultsetTransfer<Timestamp>
{
    
    public TimeStampTransfer(Class<Timestamp> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    protected Timestamp valueOf(ResultSet resultSet) throws Exception
    {
        return resultSet.getTimestamp(1);
    }
    
}
