package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetMapArrayMethodInfo extends AbstractArrayReadMethodInfo
{
    
    public SetMapArrayMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
    }
    
    @Override
    protected void readOneDim(String bk)
    {
        Type type = method.getGenericParameterTypes()[0];
        while (type instanceof GenericArrayType)
        {
            type = ((GenericArrayType) type).getGenericComponentType();
        }
        Class<?> rawClass = ((Class<?>) ((ParameterizedType) type).getRawType());
        if (rawClass.isInterface() || Modifier.isAbstract(rawClass.getModifiers()))
        {
            throw new RuntimeException(StringUtil.format("反序列必须有足够的信息，方法的入参类型只能是类，不能是接口。请检查{}.{}", method.getDeclaringClass().getName(), method.getName()));
        }
        else
        {
            str += bk + rawClass.getName() + " map = new " + rawClass.getName() + "();\n";
            str += bk + "JsonObject jsonObject0 = jsonArray1.getJsonObject(i1);\n";
            str += bk + "Iterator it = jsonObject0.entrySet().iterator();\n";
            str += bk + "Object key = null;\n";
            str += bk + "Object value = null;\n";
            str += bk + "while(it.hasNext())\n";
            str += bk + "{\n";
            str += bk + "\tjava.util.Map.Entry each = (java.util.Map.Entry)it.next();\n";
            if (((ParameterizedType) type).getActualTypeArguments()[0] instanceof Class)
            {
                Class<?> realType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                if (realType.equals(String.class))
                {
                    str += bk + "\tkey = (String)each.getKey();\n";
                }
                else if (realType.equals(Character.class))
                {
                    str += bk + "\tkey = ((String)each.getKey()).charAt(0);\n";
                }
                else if (wrapperSet.contains(realType))
                {
                    str += bk + "\tkey = " + realType.getSimpleName() + ".valueOf(each.getKey());\n";
                }
                else
                {
                    str += bk + "\tkey = ReaderContext.read(" + realType.getName() + ".class,each.getKey());\n";
                }
            }
            else
            {
                str += bk + "\tkey = (String)each.getKey();\n";
            }
            if (((ParameterizedType) type).getActualTypeArguments()[1] instanceof Class)
            {
                Class<?> realType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[1];
                if (wrapperSet.contains(realType))
                {
                    str += bk + "\tvalue = jsonObject0.getW" + realType.getSimpleName() + "(each.getKey());\n";
                }
                else
                {
                    str += bk + "\tvalue = ReaderContext.read(" + realType.getName() + ".class,each.getValue());\n";
                }
            }
            else
            {
                str += bk + "\tvalue = each.getValue();\n";
            }
            str += bk + "\tmap.put(key,value);\n";
            str += bk + "}\n";
            str += bk + "array1[i1] = map;\n";
        }
    }
    
}
