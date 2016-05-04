package com.jfireframework.baseutil.reflect.copy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.jfireframework.baseutil.reflect.ReflectUtil;

public class CopyUtilImpl<T, D> implements CopyUtil<T, D>
{
    private CopyField[] copyFields;
    
    public CopyUtilImpl(Class<T> src, Class<D> des)
    {
        Field[] srcFields = ReflectUtil.getAllFields(src);
        Field[] desFields = ReflectUtil.getAllFields(des);
        HashMap<String, Field> fieldMap = new HashMap<>();
        for (Field each : srcFields)
        {
            fieldMap.put(each.getName(), each);
        }
        List<CopyField> copyFields = new ArrayList<>();
        for (Field each : desFields)
        {
            if (Modifier.isStatic(each.getType().getModifiers()) || Modifier.isFinal(each.getType().getModifiers()))
            {
                continue;
            }
            if (fieldMap.containsKey(each.getName()))
            {
                Field srcField = fieldMap.get(each.getName());
                if (srcField.getType() == each.getType())
                {
                    if (srcField.getType() == int.class)
                    {
                        copyFields.add(new CopyField.IntField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == byte.class)
                    {
                        copyFields.add(new CopyField.ByteField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == long.class)
                    {
                        copyFields.add(new CopyField.LongField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == short.class)
                    {
                        copyFields.add(new CopyField.ShortField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == boolean.class)
                    {
                        copyFields.add(new CopyField.BooleanField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == double.class)
                    {
                        copyFields.add(new CopyField.DoubleField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == float.class)
                    {
                        copyFields.add(new CopyField.FloatField(srcField, each));
                        continue;
                    }
                    if (srcField.getType() == char.class)
                    {
                        copyFields.add(new CopyField.CharField(srcField, each));
                        continue;
                    }
                    copyFields.add(new CopyField.ObjectField(srcField, each));
                    continue;
                }
            }
        }
        this.copyFields = copyFields.toArray(new CopyField[0]);
    }
    
    @Override
    public D copy(T src, D desc)
    {
        for (CopyField each : copyFields)
        {
            each.copy(src, desc);
        }
        return desc;
    }
    
}
