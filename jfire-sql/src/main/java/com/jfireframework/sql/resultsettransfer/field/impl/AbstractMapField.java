package com.jfireframework.sql.resultsettransfer.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;
import com.jfireframework.sql.resultsettransfer.field.MapField;
import sun.misc.Unsafe;

/**
 * 基础CURD操作映射的抽象属性类
 * 
 * @author linbin
 * 
 */
public abstract class AbstractMapField implements MapField
{
    protected final long          offset;
    protected final String        dbColName;
    protected final static Unsafe unsafe = ReflectUtil.getUnsafe();
    protected final boolean       saveIgnore;
    protected boolean             loadIgnore;
    protected final Field         field;
    protected int                 length;
    
    public AbstractMapField(Field field, NameStrategy nameStrategy)
    {
        offset = unsafe.objectFieldOffset(field);
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
            loadIgnore = column.loadIgnore();
            saveIgnore = column.saveIgnore();
            length = column.length();
        }
        else
        {
            length = -1;
            saveIgnore = false;
            loadIgnore = false;
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
    public boolean loadIgnore()
    {
        return loadIgnore;
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
    
    /**
     * 返回原始的field对象
     * 
     * @return
     */
    @Override
    public Field getField()
    {
        return field;
    }
}
