package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetCollectionArrayMethodInfo extends AbstractArrayReadMethodInfo
{
    
    public SetCollectionArrayMethodInfo(Method method, ReadStrategy strategy)
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
            str += bk + rawClass.getName() + " collection = new " + rawClass.getName() + "();\n";
            str += bk + "JsonArray jsonArray0 = jsonArray1.getJsonArray(i1);\n";
            str += bk + "int size = jsonArray0.size();\n";
            str += bk + "for(int i0=0;i0<size;i0++)\n";
            str += bk + "{\n";
            if (((ParameterizedType) type).getActualTypeArguments()[0] instanceof Class)
            {
                Class<?> realType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                if (wrapperSet.contains(realType))
                {
                    str += bk + "\tcollection.add(jsonArray0.getW" + realType.getSimpleName() + "(i0));\n";
                }
                else
                {
                    str += bk + "\tcollection.add(ReaderContext.read(" + realType.getName() + ".class,jsonArray0.get(i0)));\n";
                }
            }
            else
            {
                str += bk + "\tcollection.add(jsonArray0.get(i0));\n";
            }
            str += bk + "}\n";
            str += bk + "array1[i1] = collection;\n";
        }
    }
    
}
