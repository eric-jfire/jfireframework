package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class FloatTransfer extends AbstractResultsetTransfer<Float>
{
    
    @Override
    protected Float valueOf(ResultSet resultSet) throws Exception
    {
        return Float.valueOf(resultSet.getFloat(1));
    }
    
}
