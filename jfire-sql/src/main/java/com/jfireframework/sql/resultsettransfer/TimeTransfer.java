package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.Time;

public class TimeTransfer extends AbstractResultsetTransfer<Time>
{
    @Override
    protected Time valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return resultSet.getTime(1);
    }
    
}
