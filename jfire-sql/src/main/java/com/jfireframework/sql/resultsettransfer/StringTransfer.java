package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTransfer extends AbstractResultsetTransfer<String>
{
    
    @Override
    protected String valueOf(ResultSet resultSet, String sql) throws SQLException
    {
        return resultSet.getString(1);
    }
    
}
