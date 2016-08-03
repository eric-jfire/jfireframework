package com.jfireframework.sql.metadata;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.field.MapField;

public class MetaData
{
    private String              tableName;
    private Map<String, String> fieldColumnMap = new HashMap<String, String>();
    private String              simpleClassName;
    
    public MetaData(Class<?> type, MapField[] mapFields)
    {
        simpleClassName = type.getSimpleName();
        if (type.isAnnotationPresent(TableEntity.class))
        {
            tableName = type.getAnnotation(TableEntity.class).name();
        }
        for (MapField each : mapFields)
        {
            fieldColumnMap.put(each.getFieldName(), each.getColName());
        }
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public String getColumnName(String fieldName)
    {
        return fieldColumnMap.get(fieldName);
    }
    
    public String getSimpleClassName()
    {
        return simpleClassName;
    }
    
}
