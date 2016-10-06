package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.sql.field.MapField;

public class VariableBeanTransfer<T> extends AbstractResultsetTransfer<T>
{
    private final Map<String, MapField> fieldMap = new HashMap<String, MapField>();
    
    public VariableBeanTransfer(Class<T> type)
    {
        super(type, null);
        for (MapField each : mapFields)
        {
            fieldMap.put(each.getColName(), each);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T transfer(ResultSet resultSet) throws Exception
    {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colNum = resultSetMetaData.getColumnCount();
        int num = 0;
        T entity = null;
        while (resultSet.next())
        {
            if (num > 1)
            {
                throw new RuntimeException("查询结果不是唯一的,请检查");
            }
            entity = (T) entityClass.newInstance();
            for (int i = 0; i < colNum; i++)
            {
                MapField mapField = fieldMap.get(resultSetMetaData.getColumnName(i + 1));
                if (mapField != null)
                {
                    mapField.setEntityValue(entity, resultSet);
                }
            }
            num++;
        }
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<T> transferList(ResultSet resultSet) throws Exception
    {
        List<T> list = new ArrayList<T>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colNum = resultSetMetaData.getColumnCount();
        while (resultSet.next())
        {
            
            T entity = (T) entityClass.newInstance();
            for (int i = 0; i < colNum; i++)
            {
                MapField mapField = fieldMap.get(resultSetMetaData.getColumnName(i + 1));
                if (mapField != null)
                {
                    mapField.setEntityValue(entity, resultSet);
                }
            }
            list.add(entity);
        }
        return list;
    }
    
    @Override
    protected T valueOf(ResultSet resultSet) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
