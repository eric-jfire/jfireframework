package com.jfireframework.codejson.methodinfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.methodinfo.impl.read.SetBaseMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.SetCollectionMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.SetCustomObjectMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.SetEnumMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.SetMapMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.SetWrapperMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.array.SetBaseArrayMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.array.SetCollectionArrayMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.array.SetCustomArrayMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.read.array.SetWarpperArrayMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnBaseMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnCustomObjectMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnEnumWriteMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnIterableMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnMapMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.ReturnWrapperMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.array.ReturnArrayBaseMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.array.ReturnArrayCustomObjectMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.array.ReturnArrayIterableMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.array.ReturnArrayMapMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.array.ReturnArrayWrapperMethodInfo;
import com.jfireframework.codejson.methodinfo.impl.write.extra.ReturnArrayListMethodInfo;

public class MethodInfoBuilder
{
    public static Set<Class<?>> wrapperSet = new HashSet<>();
    
    static
    {
        wrapperSet.add(String.class);
        wrapperSet.add(Boolean.class);
        wrapperSet.add(Integer.class);
        wrapperSet.add(Long.class);
        wrapperSet.add(Short.class);
        wrapperSet.add(Float.class);
        wrapperSet.add(Double.class);
        wrapperSet.add(Short.class);
        wrapperSet.add(Byte.class);
        wrapperSet.add(Character.class);
        wrapperSet.equals(String.class);
    }
    
    /**
     * 给定一个(get)Method，创建一个用于返回json文本的methodinfo对象
     * 
     * @param method
     * @return
     */
    public static WriteMethodInfo buildWriteMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        Class<?> returnType = method.getReturnType();
        if (returnType.isArray())
        {
            return buildWriteArray(method, strategy, entityName);
        }
        else
        {
            return buildWriteSingle(method, strategy, entityName);
        }
    }
    
    private static WriteMethodInfo buildWriteArray(Method method, WriteStrategy strategy, String entityName)
    {
        Class<?> resultType = method.getReturnType();
        Class<?> rootType = getRootType(resultType);
        if (rootType.isPrimitive())
        {
            return new ReturnArrayBaseMethodInfo(method, strategy, entityName);
        }
        else if (wrapperSet.contains(rootType))
        {
            return new ReturnArrayWrapperMethodInfo(method, strategy, entityName);
        }
        else if (Iterable.class.isAssignableFrom(rootType))
        {
            return new ReturnArrayIterableMethodInfo(method, strategy, entityName);
        }
        else if (Map.class.isAssignableFrom(rootType))
        {
            return new ReturnArrayMapMethodInfo(method, strategy, entityName);
        }
        else
        {
            return new ReturnArrayCustomObjectMethodInfo(method, strategy, entityName);
        }
    }
    
    private static Class<?> getRootType(Class<?> type)
    {
        while (type.isArray())
        {
            type = type.getComponentType();
        }
        return type;
    }
    
    /**
     * 如果方法的返回结果不是数组，则使用该方法创建MethodInfo对象
     * 
     * @param method
     * @return
     */
    private static WriteMethodInfo buildWriteSingle(Method method, WriteStrategy strategy, String entityName)
    {
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive())
        {
            return new ReturnBaseMethodInfo(method, strategy, entityName);
        }
        else if (wrapperSet.contains(returnType))
        {
            return new ReturnWrapperMethodInfo(method, strategy, entityName);
        }
        else if (ArrayList.class.isAssignableFrom(returnType))
        {
            return new ReturnArrayListMethodInfo(method, strategy, entityName);
        }
        else if (Iterable.class.isAssignableFrom(returnType))
        {
            return new ReturnIterableMethodInfo(method, strategy, entityName);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            return new ReturnMapMethodInfo(method, strategy, entityName);
        }
        else if (returnType.isEnum())
        {
            return new ReturnEnumWriteMethodInfo(method, strategy, entityName);
        }
        else
        {
            return new ReturnCustomObjectMethodInfo(method, strategy, entityName);
        }
    }
    
    /**
     * 根据类的方法属性，创建该方法的代码输出体
     * 
     * @param method
     * @return
     */
    public static ReadMethodInfo buildReadMethodInfo(Method method, ReadStrategy strategy)
    {
        if (getParamType(method).isArray())
        {
            return buildArrayRead(method, strategy);
        }
        else
        {
            return buildSingleRead(method, strategy);
        }
    }
    
    private static ReadMethodInfo buildSingleRead(Method method, ReadStrategy strategy)
    {
        Class<?> paramType = getParamType(method);
        if (paramType.isPrimitive())
        {
            return new SetBaseMethodInfo(method, strategy);
        }
        else if (wrapperSet.contains(paramType))
        {
            return new SetWrapperMethodInfo(method, strategy);
        }
        else if (Collection.class.isAssignableFrom(paramType))
        {
            return new SetCollectionMethodInfo(method, strategy);
        }
        else if (Map.class.isAssignableFrom(paramType))
        {
            return new SetMapMethodInfo(method, strategy);
        }
        else if (paramType.isEnum())
        {
            return new SetEnumMethodInfo(method, strategy);
        }
        else
        {
            return new SetCustomObjectMethodInfo(method, strategy);
        }
    }
    
    private static ReadMethodInfo buildArrayRead(Method method, ReadStrategy strategy)
    {
        Class<?> paramType = getParamType(method);
        Class<?> rootType = getRootType(paramType);
        if (rootType.isPrimitive())
        {
            return new SetBaseArrayMethodInfo(method, strategy);
        }
        else if (wrapperSet.contains(rootType))
        {
            return new SetWarpperArrayMethodInfo(method, strategy);
        }
        else if (Collection.class.isAssignableFrom(rootType))
        {
            return new SetCollectionArrayMethodInfo(method, strategy);
        }
        else
        {
            return new SetCustomArrayMethodInfo(method, strategy);
        }
    }
    
    private static Class<?> getParamType(Method method)
    {
        return method.getParameterTypes()[0];
    }
}
