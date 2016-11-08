package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class BooleanTransfer extends AbstractResultsetTransfer<Boolean>
{
    
    @Override
    protected Boolean valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return Boolean.valueOf(resultSet.getBoolean(1));
    }
    
}
