package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.util.Date;

public class UtilDateTransfer extends AbstractResultsetTransfer<Date>
{
    
    public UtilDateTransfer(Class<Date> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    protected Date valueOf(ResultSet resultSet) throws Exception
    {
        return resultSet.getDate(1);
    }
    
}
