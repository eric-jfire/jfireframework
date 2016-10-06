package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;

public class BooleanTransfer extends AbstractResultsetTransfer<Boolean>
{
    
    public BooleanTransfer(Class<Boolean> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    protected Boolean valueOf(ResultSet resultSet) throws Exception
    {
        return Boolean.valueOf(resultSet.getBoolean(1));
    }
    
}
