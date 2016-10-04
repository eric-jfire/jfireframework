package com.jfireframework.sql.resultsettransfer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dbstructure.NameStrategy;
import com.jfireframework.sql.field.MapField;
import com.jfireframework.sql.field.MapFieldBuilder;

public abstract class AbstractResultsetTransfer<T> implements ResultSetTransfer<T>
{
    protected final MapField[] mapFields;
    protected final Class<?>   entityClass;
    
    public AbstractResultsetTransfer(Class<T> entityClass)
    {
        this.entityClass = entityClass;
        mapFields = null;
    }
    
    public AbstractResultsetTransfer(Class<T> entityClass, String fieldNames)
    {
        this.entityClass = entityClass;
        NameStrategy nameStrategy;
        try
        {
            nameStrategy = entityClass.getAnnotation(TableEntity.class).nameStrategy().newInstance();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        List<MapField> list = new ArrayList<MapField>();
        if (fieldNames == null || fieldNames.equals("*"))
        {
            for (Field each : ReflectUtil.getAllFields(entityClass))
            {
                if (each.isAnnotationPresent(SqlIgnore.class) || Map.class.isAssignableFrom(each.getType()) || List.class.isAssignableFrom(each.getType()) || each.getType().isInterface() || each.getType().isArray() || Modifier.isStatic(each.getModifiers()))
                {
                    continue;
                }
                list.add(MapFieldBuilder.buildMapField(each, nameStrategy));
            }
            mapFields = list.toArray(new MapField[list.size()]);
        }
        else
        {
            Map<String, MapField> map = new HashMap<String, MapField>();
            for (Field each : ReflectUtil.getAllFields(entityClass))
            {
                if (each.isAnnotationPresent(SqlIgnore.class) || Map.class.isAssignableFrom(each.getType()) || List.class.isAssignableFrom(each.getType()) || each.getType().isInterface() || each.getType().isArray() || Modifier.isStatic(each.getModifiers()))
                {
                    continue;
                }
                map.put(each.getName(), MapFieldBuilder.buildMapField(each, nameStrategy));
            }
            for (String each : fieldNames.split(","))
            {
                list.add(map.get(each));
            }
            mapFields = list.toArray(new MapField[list.size()]);
        }
    }
}
