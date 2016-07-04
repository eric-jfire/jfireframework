package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.field.MapField;
import sun.misc.Unsafe;

/**
 * 基础CURD操作映射的抽象属性类
 * 
 * @author linbin
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractMapField implements MapField
{
    protected long          offset;
    protected final String  dbColName;
    protected static Unsafe unsafe     = ReflectUtil.getUnsafe();
    protected boolean       saveIgnore = false;
    protected Field         field;
    protected int           length;
    
    public AbstractMapField(Field field)
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
                dbColName = toDbColName(field.getName());
            }
            saveIgnore = field.getAnnotation(Column.class).saveIgnore();
            length = field.getAnnotation(Column.class).length();
        }
        else
        {
            dbColName = toDbColName(field.getName());
        }
    }
    
    private String toDbColName(String name)
    {
        StringCache cache = new StringCache(20);
        int index = 0;
        while (index < name.length())
        {
            char c = name.charAt(index);
            if (c >= 'A' && c <= 'Z')
            {
                cache.append('_').append(Character.toLowerCase(c));
            }
            else
            {
                cache.append(c);
            }
            index += 1;
        }
        return cache.toString();
    }
    
    @Override
    public String getColName()
    {
        return dbColName;
    }
    
    public boolean saveIgnore()
    {
        return saveIgnore;
    }
    
    public String getFieldName()
    {
        return field.getName();
    }
    
    public Class<?> getFieldType()
    {
        return field.getType();
    }
    
    public int getDbLength()
    {
        return length;
    }
}
