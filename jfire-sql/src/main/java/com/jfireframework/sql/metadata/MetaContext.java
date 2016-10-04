package com.jfireframework.sql.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class MetaContext
{
    private final Map<String, TableMetaData> entityMap = new HashMap<String, TableMetaData>();
    private final TableMetaData[]            metaDatas;
    
    public MetaContext(Set<Class<?>> set, NameStrategy nameStrategy) throws ClassNotFoundException
    {
        List<TableMetaData> tableMetaDatas = new LinkedList<TableMetaData>();
        for (Class<?> each : set)
        {
            if (each.isAnnotationPresent(TableEntity.class))
            {
                tableMetaDatas.add(new TableMetaData(each, nameStrategy));
            }
        }
        for (TableMetaData each : tableMetaDatas)
        {
            entityMap.put(each.getEntityClass().getSimpleName(), each);
        }
        metaDatas = tableMetaDatas.toArray(new TableMetaData[tableMetaDatas.size()]);
    }
    
    public TableMetaData get(String entityClassSimpleName)
    {
        return entityMap.get(entityClassSimpleName);
    }
    
    public TableMetaData[] metaDatas()
    {
        return metaDatas;
    }
}
