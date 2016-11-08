package com.jfireframework.sql.resultsettransfer;

import java.sql.Date;
import java.sql.ResultSet;

public class SqlDateTransfer extends AbstractResultsetTransfer<java.sql.Date>
{
    
    @Override
    protected Date valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return resultSet.getDate(1);
    }
    
}
