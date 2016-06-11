package com.jfireframework.sql.function.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.field.MapField;
import com.jfireframework.sql.field.MapFieldBuilder;
import com.jfireframework.sql.function.MapBean;

public class MapBeanImpl<T> implements MapBean<T>
{
    private MapField[]                mapFields;
    private HashMap<String, MapField> fieldMap = new HashMap<String, MapField>();
    private Class<T>                  entityClass;
    
    public MapBeanImpl(Class<T> entityClass)
    {
        this.entityClass = entityClass;
        LightSet<MapField> set = new LightSet<MapField>();
        for (Field each : ReflectUtil.getAllFields(entityClass))
        {
            if (each.isAnnotationPresent(SqlIgnore.class) || Map.class.isAssignableFrom(each.getType()) || List.class.isAssignableFrom(each.getType()) || each.getType().isInterface() || each.getType().isArray() || Modifier.isStatic(each.getModifiers()))
            {
                continue;
            }
            set.add(MapFieldBuilder.buildMapField(each));
        }
        mapFields = set.toArray(MapField.class);
        for (MapField each : mapFields)
        {
            fieldMap.put(each.getColName(), each);
        }
    }
    
    @Override
    public List<T> toBean(ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException
    {
        List<T> list = new ArrayList<T>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colNum = resultSetMetaData.getColumnCount();
        MapField[] mapFields = new MapField[colNum];
        int index = 0;
        MapField mapField = null;
        for (int i = 0; i < colNum; i++)
        {
            mapField = fieldMap.get(resultSetMetaData.getColumnLabel(i + 1));
            if (mapField != null)
            {
                mapFields[index++] = mapField;
            }
        }
        while (resultSet.next())
        {
            T entity = entityClass.newInstance();
            for (int i = 0; i < index; i++)
            {
                mapFields[i].setEntityValue(entity, resultSet);
            }
            list.add(entity);
        }
        return list;
    }
    
    @Override
    public T singleResultToBean(ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException
    {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colNum = resultSetMetaData.getColumnCount();
        int num = 0;
        T entity = entityClass.newInstance();
        while (resultSet.next())
        {
            for (int i = 0; i < colNum; i++)
            {
                MapField mapField = fieldMap.get(resultSetMetaData.getColumnName(i + 1));
                if (mapField != null)
                {
                    mapField.setEntityValue(entity, resultSet);
                }
            }
            num++;
            if (num > 1)
            {
                throw new RuntimeException("查询结果不是唯一的,请检查");
            }
        }
        if (num == 0)
        {
            return null;
        }
        else
        {
            return entity;
        }
    }
    
}
