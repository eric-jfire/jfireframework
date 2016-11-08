package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.Timestamp;

public class TimeStampTransfer extends AbstractResultsetTransfer<Timestamp>
{
    
    @Override
    protected Timestamp valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return resultSet.getTimestamp(1);
    }
    
}
