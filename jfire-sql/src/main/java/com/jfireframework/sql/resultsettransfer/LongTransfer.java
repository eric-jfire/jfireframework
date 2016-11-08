package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class LongTransfer extends AbstractResultsetTransfer<Long>
{
    @Override
    protected Long valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return Long.valueOf(resultSet.getLong(1));
    }
    
}
