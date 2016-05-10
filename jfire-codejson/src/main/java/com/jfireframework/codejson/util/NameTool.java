package com.jfireframework.codejson.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.codejson.annotation.JsonRename;
import com.jfireframework.codejson.function.Strategy;

public class NameTool
{
    public static String getNameFromMethod(Method method, Strategy strategy)
    {
        String fieldName = ReflectUtil.getFieldNameFromMethod(method);
        if (strategy != null)
        {
            if (strategy.containsRename(method.getDeclaringClass().getName() + '.' + fieldName))
            {
                return strategy.getRename(method.getDeclaringClass().getName() + '.' + fieldName);
            }
            else
            {
                return fieldName;
            }
        }
        if (method.isAnnotationPresent(JsonRename.class))
        {
            JsonRename rename = method.getAnnotation(JsonRename.class);
            return rename.value();
        }
        else
        {
            try
            {
                Field field = method.getDeclaringClass().getDeclaredField(fieldName);
                if (field.isAnnotationPresent(JsonRename.class))
                {
                    return field.getAnnotation(JsonRename.class).value();
                }
                else
                {
                    return fieldName;
                }
            }
            catch (Exception e)
            {
                return fieldName;
            }
        }
    }
    
    /**
     * 获取数组的根类型
     * 
     * @param type
     * @return
     */
    public static Class<?> getRootType(Class<?> type)
    {
        while (type.isArray())
        {
            type = type.getComponentType();
        }
        return type;
    }
    
    /**
     * 创建数组类型的字段表示，比如int[][]这样的字符串
     * 
     * @param rootName
     * @param dim
     * @return
     */
    public static String buildDimTypeName(String rootName, int dim)
    {
        for (int i = 0; i < dim; i++)
        {
            rootName += "[]";
        }
        return rootName;
    }
    
    public static String buildNewDimTypeName(String rootName, int dim, String size)
    {
        String value = "new " + rootName + "[" + size + "]";
        dim--;
        for (int i = 0; i < dim; i++)
        {
            value += "[]";
        }
        return value;
    }
    
    /**
     * 获得该数组的维度
     * 
     * @param type
     * @return
     */
    public static int getDimension(Class<?> type)
    {
        int dim = 0;
        while (type.isArray())
        {
            dim++;
            type = type.getComponentType();
        }
        return dim;
    }
    
}
