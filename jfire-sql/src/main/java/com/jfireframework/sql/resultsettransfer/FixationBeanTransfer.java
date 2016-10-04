package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.sql.field.MapField;

public class FixationBeanTransfer<T> extends AbstractResultsetTransfer<T>
{
    public FixationBeanTransfer(Class<T> type, String fieldNames)
    {
        super(type, fieldNames);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T transfer(ResultSet resultSet) throws Exception
    {
        int num = 0;
        T entity = null;
        while (resultSet.next())
        {
            if (num > 1)
            {
                throw new RuntimeException("查询结果不是唯一的,请检查");
            }
            entity = (T) entityClass.newInstance();
            for (MapField each : mapFields)
            {
                each.setEntityValue(entity, resultSet);
            }
            num++;
        }
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<T> transferList(ResultSet resultSet) throws Exception
    {
        List<T> list = new LinkedList<T>();
        while (resultSet.next())
        {
            T entity = (T) entityClass.newInstance();
            for (MapField each : mapFields)
            {
                each.setEntityValue(entity, resultSet);
            }
            list.add(entity);
        }
        return list;
    }
    
}
