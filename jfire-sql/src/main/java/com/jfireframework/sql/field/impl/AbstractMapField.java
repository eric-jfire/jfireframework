package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.StringUtil;
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
    protected String        dbColName;
    protected static Unsafe unsafe     = ReflectUtil.getUnsafe();
    protected boolean       saveIgnore = false;
    protected Field         field;
    protected int           length;
                            
    public AbstractMapField(Field field)
    {
        offset = unsafe.objectFieldOffset(field);
        this.field = field;
        dbColName = field.getName();
        if (field.isAnnotationPresent(Column.class) && StringUtil.isNotBlank(field.getAnnotation(Column.class).name()))
        {
            dbColName = field.getAnnotation(Column.class).name();
        }
        if (field.isAnnotationPresent(Column.class))
        {
            saveIgnore = field.getAnnotation(Column.class).saveIgnore();
        }
        if (field.isAnnotationPresent(Column.class))
        {
            length = field.getAnnotation(Column.class).length();
        }
        else
        {
            length = -1;
        }
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
