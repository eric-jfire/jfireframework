package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import com.jfireframework.sql.field.MapField;

public class FixationBeanTransfer<T> extends AbstractResultsetTransfer<T>
{
    public FixationBeanTransfer(Class<T> type, String fieldNames)
    {
        super(type, fieldNames);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected T valueOf(ResultSet resultSet) throws Exception
    {
        T entity = (T) entityClass.newInstance();
        for (MapField each : mapFields)
        {
            each.setEntityValue(entity, resultSet);
        }
        return entity;
    }
    
}
