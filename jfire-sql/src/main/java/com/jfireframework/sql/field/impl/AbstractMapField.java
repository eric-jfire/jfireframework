package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.dbstructure.NameStrategy;
import com.jfireframework.sql.field.MapField;
import sun.misc.Unsafe;

/**
 * 基础CURD操作映射的抽象属性类
 * 
 * @author linbin
 * 
 */
public abstract class AbstractMapField implements MapField
{
    protected long          offset;
    protected final String  dbColName;
    protected static Unsafe unsafe     = ReflectUtil.getUnsafe();
    protected boolean       saveIgnore = false;
    protected Field         field;
    protected int           length;
    
    public AbstractMapField(Field field, NameStrategy nameStrategy)
    {
        offset = unsafe.objectFieldOffset(field);
        this.field = field;
        length = -1;
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
            saveIgnore = field.getAnnotation(Column.class).saveIgnore();
            length = field.getAnnotation(Column.class).length();
        }
        else
        {
            dbColName = nameStrategy.toDbName(field.getName());
        }
    }
    
    @Override
    public String getColName()
    {
        return dbColName;
    }
    
    @Override
    public boolean saveIgnore()
    {
        return saveIgnore;
    }
    
    @Override
    public String getFieldName()
    {
        return field.getName();
    }
    
    @Override
    public Class<?> getFieldType()
    {
        return field.getType();
    }
    
    @Override
    public int getDbLength()
    {
        return length;
    }
}
