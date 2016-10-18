package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

public class UtilDateTransfer extends AbstractResultsetTransfer<Date>
{
    public UtilDateTransfer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    protected Date valueOf(ResultSet resultSet) throws Exception
    {
        Timestamp timestamp = resultSet.getTimestamp(1);
        return new Date(timestamp.getTime());
    }
    
}
