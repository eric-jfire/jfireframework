package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.util.NameTool;

public class SetMapMethodInfo extends AbstractReadMethodInfo
{
    
    public SetMapMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        str = "if(json.contains(\"" + NameTool.getNameFromMethod(method, strategy) + "\"))\n";
        str += "{\n";
        Class<?> paramType = getParamType();
        if (paramType.isInterface() || Modifier.isAbstract(paramType.getModifiers()))
        {
            throw new RuntimeException(StringUtil.format("反序列必须有足够的信息，方法的入参类型只能是类，不能是接口。请检查{}.{}", method.getDeclaringClass().getName(), method.getName()));
        }
        else
        {
            str += "\t" + paramType.getName() + " map = new " + paramType.getName() + "();\n";
        }
        str += "\tJsonObject jsonObject = json.getJsonObject(\"" + NameTool.getNameFromMethod(method, strategy) + "\");\n";
        str += "\tIterator it = jsonObject.entrySet().iterator();\n";
        str += "\tObject key = null;\n";
        str += "\tObject value = null;\n";
        str += "\twhile(it.hasNext())\n";
        str += "\t{\n";
        str += "\t\tjava.util.Map.Entry each = (java.util.Map.Entry)it.next();\n";
        if (((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0] instanceof Class)
        {
            Class<?> keyType = (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
            if (keyType.equals(String.class))
            {
                str += "\t\tkey = (String)each.getKey();\n";
            }
            else if (keyType.equals(Character.class))
            {
                str += "\t\tkey = ((String)each.getKey()).charAt(0);\n";
            }
            else if (wrapperSet.contains(keyType))
            {
                str += "\t\tkey = " + keyType.getName() + ".valueOf((String)each.getKey());\n";
            }
            else
            {
                str += "\t\tkey = ReaderContext.read(" + keyType.getName() + ".class,(String)each.getKey());\n";
            }
            
        }
        else
        {
            str += "\t\tkey = (String)each.getKey();\n";
        }
        if (((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[1] instanceof Class)
        {
            Class<?> valueType = (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[1];
            if (valueType.equals(String.class))
            {
                str += "\t\tvalue = (String)each.getValue();\n";
            }
            else if (valueType.equals(Character.class))
            {
                str += "\t\tvalue = ((String)each.getValue()).charAt(0);\n";
            }
            else if (wrapperSet.contains(valueType))
            {
                str += "\t\tvalue = jsonObject.getW" + valueType.getSimpleName() + "(each.getKey());\n";
            }
            else
            {
                str += "\t\tvalue = ReaderContext.read(" + valueType.getName() + ".class,each.getValue());\n";
            }
        }
        else
        {
            str += "\t\t value = each.getValue();\n";
        }
        str += "\t\tmap.put(key,value);\n";
        str += "\t}\n";
        str += "\t" + entityName + method.getName() + "(map);\n";
        str += "}\n";
        
    }
    
}
