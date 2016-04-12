package com.jfireframework.baseutil.copy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.jfireframework.baseutil.annotation.Rename;
import com.jfireframework.baseutil.collection.map.Entry;
import com.jfireframework.baseutil.collection.map.LightMap;
import com.jfireframework.baseutil.copy.field.CopyField;
import com.jfireframework.baseutil.copy.field.impl.BooleanField;
import com.jfireframework.baseutil.copy.field.impl.FloatField;
import com.jfireframework.baseutil.copy.field.impl.IntField;
import com.jfireframework.baseutil.copy.field.impl.LongField;
import com.jfireframework.baseutil.copy.field.impl.ObjectCopyField;
import com.jfireframework.baseutil.reflect.ReflectUtil;

public class CopyUtil
{
    // 需要复制的数据的属性
    private CopyField[] copyFields;
    
    private CopyUtil(CopyField[] copyFields)
    {
        this.copyFields = copyFields;
    }
    
    /**
     * 将源对象的内容拷贝到目标对象。
     * 两个入参的顺序必须和生成工具类的顺序一致
     * 
     * @param src
     * @param target
     * @author windfire(windfire@zailanghua.com)
     */
    public void copy(Object src, Object target)
    {
        for (CopyField each : copyFields)
        {
            each.copy(src, target);
        }
    }
    
    /**
     * 分析源对象和目标对象，生成一个拷贝工具类。
     * 静态属性和final属性不会被拷贝
     * 注意：生成工具类的分析过程有一定的消耗，建议使用静态属性存储该工具类的实例
     * 
     * @param srcClass 源对象
     * @param targetClass 目标对象，目标对象中的属性名称和属性类型均需要对应才会被拷贝
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public static CopyUtil build(Class<?> srcClass, Class<?> targetClass)
    {
        Field[] allSrcFields = ReflectUtil.getAllFields(srcClass);
        Field[] allTargetFields = ReflectUtil.getAllFields(targetClass);
        int maxSize = allSrcFields.length > allTargetFields.length ? allSrcFields.length : allTargetFields.length;
        LightMap<Field, Field> map = new LightMap<>(maxSize);
        for (int i = 0; i < allTargetFields.length; i++)
        {
            Field targetField = allTargetFields[i];
            for (int j = 0; j < allSrcFields.length; j++)
            {
                Field srcField = allSrcFields[i];
                if (canBeCopy(srcField, targetField))
                {
                    map.put(srcField, targetField);
                }
            }
        }
        int index = 0;
        CopyField[] copyFields = new CopyField[map.getCount()];
        for (Entry<Field, Field> entry : map.getEntries())
        {
            CopyField copyField = buildField(entry.getKey(), entry.getValue());
            if (copyField != null)
            {
                copyFields[index++] = copyField;
            }
        }
        return new CopyUtil(copyFields);
    }
    
    private static boolean canBeCopy(Field srcField, Field targetField)
    {
        if (Modifier.isStatic(targetField.getModifiers()) || Modifier.isFinal(targetField.getModifiers()))
        {
            return false;
        }
        String targetFieldName = targetField.isAnnotationPresent(Rename.class) ? targetField.getAnnotation(Rename.class).value() : targetField.getName();
        String srcFieldName = srcField.isAnnotationPresent(Rename.class) ? srcField.getAnnotation(Rename.class).value() : srcField.getName();
        if (targetFieldName.equals(srcFieldName) && targetField.getType().equals(srcField.getType()))
        {
            Class<?> type = targetField.getType();
            if (type.equals(Integer.class) || type.equals(Long.class) || type.equals(Boolean.class) || type.equals(String.class) || type.equals(Float.class) || type.isPrimitive())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        
    }
    
    private static CopyField buildField(Field srcField, Field targetField)
    {
        Class<?> type = srcField.getType();
        if (type.equals(int.class))
        {
            return new IntField(srcField, targetField);
        }
        if (type.equals(boolean.class))
        {
            return new BooleanField(srcField, targetField);
        }
        if (type.equals(float.class))
        {
            return new FloatField(srcField, targetField);
        }
        if (type.equals(long.class))
        {
            return new LongField(srcField, targetField);
        }
        return new ObjectCopyField(srcField, targetField);
    }
}
