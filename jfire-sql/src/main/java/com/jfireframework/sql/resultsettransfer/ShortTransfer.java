package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class ShortTransfer extends AbstractResultsetTransfer<Short>
{
    
    @Override
    protected Short valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return Short.valueOf(resultSet.getShort(1));
    }
    
}
