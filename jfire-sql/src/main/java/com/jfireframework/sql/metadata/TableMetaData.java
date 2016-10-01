package com.jfireframework.sql.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.IdStrategy;
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class TableMetaData
{
    private final String      tableName;
    private final FieldInfo[] fieldInfos;
    private final FieldInfo   idInfo;
    private final IdStrategy  idStrategy;
    
    public static class FieldInfo
    {
        private final String dbColName;
        private final String fieldName;
        private final Field  field;
        private final int    length;
        
        public FieldInfo(Field field, NameStrategy nameStrategy)
        {
            fieldName = field.getName();
            this.field = field;
            if (field.isAnnotationPresent(Column.class))
            {
                Column column = field.getAnnotation(Column.class);
                if (StringUtil.isNotBlank(column.name()))
                {
                    dbColName = field.getAnnotation(Column.class).name();
                }
                else
                {
                    dbColName = nameStrategy.toDbName(field.getName());
                }
                length = field.getAnnotation(column.getClass()).length();
            }
            else
            {
                dbColName = nameStrategy.toDbName(field.getName());
                length = -1;
            }
        }
        
        public String getDbColName()
        {
            return dbColName;
        }
        
        public String getFieldName()
        {
            return fieldName;
        }
        
        public Field getField()
        {
            return field;
        }
        
        public int getLength()
        {
            return length;
        }
        
    }
    
    public TableMetaData(Class<?> ckass, NameStrategy nameStrategy)
    {
        TableEntity entity = ckass.getAnnotation(TableEntity.class);
        tableName = entity.name();
        List<FieldInfo> list = new LinkedList<FieldInfo>();
        Field t_idField = null;
        for (Field each : ReflectUtil.getAllFields(ckass))
        {
            if (notTableField(each))
            {
                continue;
            }
            list.add(new FieldInfo(each, nameStrategy));
            if (each.isAnnotationPresent(Id.class))
            {
                t_idField = each;
            }
        }
        fieldInfos = list.toArray(new FieldInfo[list.size()]);
        idInfo = new FieldInfo(t_idField, nameStrategy);
        idStrategy = t_idField.getType().getAnnotation(Id.class).idStrategy();
    }
    
    private boolean notTableField(Field field)
    {
        if (field.isAnnotationPresent(SqlIgnore.class) //
                || Map.class.isAssignableFrom(field.getType())//
                || List.class.isAssignableFrom(field.getType())//
                || field.getType().isInterface()//
                || field.getType().isArray()//
                || Modifier.isStatic(field.getModifiers())//
                || (field.isAnnotationPresent(Column.class) && (field.getAnnotation(Column.class).daoIgnore() || field.getAnnotation(Column.class).saveIgnore())))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public IdStrategy getIdStrategy()
    {
        return idStrategy;
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public FieldInfo[] getFieldInfos()
    {
        return fieldInfos;
    }
    
    public FieldInfo getIdInfo()
    {
        return idInfo;
    }
    
}
