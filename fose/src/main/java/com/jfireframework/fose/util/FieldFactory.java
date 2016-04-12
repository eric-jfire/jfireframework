package com.jfireframework.fose.util;

import java.lang.reflect.Field;
import java.util.Date;
import com.jfireframework.fose.field.CacheField;
import com.jfireframework.fose.field.CustomObjectField;
import com.jfireframework.fose.field.DirectObjectField;
import com.jfireframework.fose.field.base.BooleanField;
import com.jfireframework.fose.field.base.ByteField;
import com.jfireframework.fose.field.base.CharField;
import com.jfireframework.fose.field.base.DoubleField;
import com.jfireframework.fose.field.base.FloatField;
import com.jfireframework.fose.field.base.IntField;
import com.jfireframework.fose.field.base.LongField;
import com.jfireframework.fose.field.base.ShortField;
import com.jfireframework.fose.field.special.DateField;
import com.jfireframework.fose.field.special.StringField;

public class FieldFactory
{
    /**
     * 根据field的类型，转换成对应的cachefield
     * 
     * @param field
     * @return
     */
    public static CacheField buildCacheField(Field field)
    {
        Class<?> fieldType = field.getType();
        while (fieldType.isArray())
        {
            // 如果是数组，则获取数组本身数据的类型
            fieldType = fieldType.getComponentType();
        }
        if (fieldType.equals(int.class))
        {
            return new IntField(field);
        }
        else if (fieldType.equals(byte.class))
        {
            return new ByteField(field);
        }
        else if (fieldType.equals(long.class))
        {
            return new LongField(field);
        }
        else if (fieldType.equals(boolean.class))
        {
            return new BooleanField(field);
        }
        else if (fieldType.equals(double.class))
        {
            return new DoubleField(field);
        }
        else if (fieldType.equals(float.class))
        {
            return new FloatField(field);
        }
        else if (fieldType.equals(char.class))
        {
            return new CharField(field);
        }
        else if (fieldType.equals(short.class))
        {
            return new ShortField(field);
        }
        else if (fieldType.equals(String.class))
        {
            return new StringField(field);
        }
        else if (fieldType.equals(Date.class))
        {
            return new DateField(field);
        }
        else if (fieldType.equals(Object.class))
        {
            return new DirectObjectField(field);
        }
        else
        {
            return new CustomObjectField(field);
        }
    }
}
