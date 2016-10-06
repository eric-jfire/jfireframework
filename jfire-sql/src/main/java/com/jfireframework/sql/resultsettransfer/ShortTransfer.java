package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class ShortTransfer extends AbstractResultsetTransfer<Short>
{
    
    public ShortTransfer(Class<Short> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    protected Short valueOf(ResultSet resultSet) throws Exception
    {
        return Short.valueOf(resultSet.getShort(1));
    }
    
}
