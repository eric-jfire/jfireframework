package com.jfireframework.sql.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
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
    private final String              tableName;
    private final FieldInfo[]         fieldInfos;
    private final FieldInfo           idInfo;
    private final IdStrategy          idStrategy;
    private final Class<?>            ckass;
    private final Map<String, String> fieldNameMap = new HashMap<String, String>();
    private final NameStrategy        nameStrategy;
    
    public static class FieldInfo
    {
        private final String  dbColName;
        private final String  fieldName;
        private final Field   field;
        private final int     length;
        private final boolean daoIgnore;
        private final boolean saveIgnore;
        
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
                length = field.getAnnotation(Column.class).length();
                daoIgnore = column.daoIgnore();
                saveIgnore = column.saveIgnore();
            }
            else
            {
                dbColName = nameStrategy.toDbName(field.getName());
                length = -1;
                daoIgnore = false;
                saveIgnore = false;
            }
        }
        
        public boolean isDaoIgnore()
        {
            return daoIgnore;
        }
        
        public boolean isSaveIgnore()
        {
            return saveIgnore;
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
        this.ckass = ckass;
        this.nameStrategy = nameStrategy;
        TableEntity entity = ckass.getAnnotation(TableEntity.class);
        tableName = entity.name();
        List<FieldInfo> list = new LinkedList<FieldInfo>();
        Field t_idField = null;
        String prefix = tableName + '.';
        for (Field each : ReflectUtil.getAllFields(ckass))
        {
            if (notTableField(each))
            {
                continue;
            }
            FieldInfo info = new FieldInfo(each, nameStrategy);
            fieldNameMap.put(prefix + info.getDbColName(), each.getName());
            list.add(info);
            if (each.isAnnotationPresent(Id.class))
            {
                t_idField = each;
            }
        }
        fieldInfos = list.toArray(new FieldInfo[list.size()]);
        if (t_idField != null)
        {
            if (t_idField.getType().isPrimitive())
            {
                throw new IllegalArgumentException("作为主键的属性不可以使用基本类型，必须使用包装类。请检查" + t_idField.getDeclaringClass().getName() + "." + t_idField.getName());
            }
            idInfo = new FieldInfo(t_idField, nameStrategy);
            idStrategy = getIdStrategy(t_idField);
        }
        else
        {
            idInfo = null;
            idStrategy = null;
        }
    }
    
    private IdStrategy getIdStrategy(Field idField)
    {
        IdStrategy idStrategy = idField.getAnnotation(Id.class).idStrategy();
        if (idStrategy == IdStrategy.autoDecision)
        {
            Class<?> type = idField.getType();
            if (type == Integer.class //
                    || type == Long.class)
            {
                return IdStrategy.nativeDb;
            }
            else if (type == String.class)
            {
                return IdStrategy.stringUid;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else
        {
            return idStrategy;
        }
    }
    
    private boolean notTableField(Field field)
    {
        if (field.isAnnotationPresent(SqlIgnore.class) //
                || Map.class.isAssignableFrom(field.getType())//
                || List.class.isAssignableFrom(field.getType())//
                || field.getType().isInterface()//
                || field.getType().isArray()//
                || Modifier.isStatic(field.getModifiers()))
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
    
    public NameStrategy getNameStrategy()
    {
        return nameStrategy;
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
    
    public Class<?> getEntityClass()
    {
        return ckass;
    }
    
    public String getFieldName(String dbColName)
    {
        return fieldNameMap.get(dbColName);
    }
}
