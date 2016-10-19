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
        HashMap<String, Field> fieldMap = new HashMap<String, Field>();
        for (Field each : srcFields)
        {
            fieldMap.put(each.getName(), each);
        }
        List<CopyField> copyFields = new ArrayList<CopyField>();
        for (Field desField : desFields)
        {
            if (
                Modifier.isStatic(desField.getModifiers()) //
                        || Modifier.isFinal(desField.getModifiers()) //
                        || desField.isAnnotationPresent(CopyIgnore.class)
            )
            {
                continue;
            }
            String copyName = desField.getName();
            if (desField.isAnnotationPresent(CopyName.class))
            {
                copyName = desField.getAnnotation(CopyName.class).value();
            }
            if (fieldMap.containsKey(copyName))
            {
                Field srcField = fieldMap.get(copyName);
                if (srcField.getType() == desField.getType())
                {
                    copyFields.add(CopyField.build(srcField, desField));
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
