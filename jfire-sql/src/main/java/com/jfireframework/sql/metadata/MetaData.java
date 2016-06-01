package com.jfireframework.sql.metadata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.annotation.TableEntity;

public class MetaData
{
    private String              tableName;
    private Map<String, String> fieldColumnMap = new HashMap<String, String>();
    private String              simpleClassName;
    
    public MetaData(Class<?> type)
    {
        simpleClassName = type.getSimpleName();
        if (type.isAnnotationPresent(TableEntity.class))
        {
            tableName = type.getAnnotation(TableEntity.class).name();
        }
        for (Field each : ReflectUtil.getAllFields(type))
        {
            if (each.isAnnotationPresent(SqlIgnore.class) || Map.class.isAssignableFrom(each.getType()) || List.class.isAssignableFrom(each.getType()) || each.getType().isInterface() || each.getType().isArray())
            {
                continue;
            }
            String dbColName = each.getName();
            if (each.isAnnotationPresent(Column.class))
            {
                dbColName = each.getAnnotation(Column.class).name();
            }
            fieldColumnMap.put(each.getName(), dbColName);
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
