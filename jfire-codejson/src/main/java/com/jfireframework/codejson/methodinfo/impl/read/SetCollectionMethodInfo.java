package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.util.NameTool;

public class SetCollectionMethodInfo extends AbstractReadMethodInfo
{
    
    public SetCollectionMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        str = "if(json.contains(\"" + NameTool.getNameFromMethod(method, strategy) + "\"))\n";
        str += "{\n";
        str += "\tJsonArray jsonArray = json.getJsonArray(\"" + NameTool.getNameFromMethod(method, strategy) + "\");\n";
        str += "\tint size = jsonArray.size();\n";
        Class<?> paramType = getParamType();
        if (paramType.isInterface() || Modifier.isAbstract(paramType.getModifiers()))
        {
            throw new RuntimeException(StringUtil.format("反序列必须有足够的信息，方法的入参类型只能是类，不能是接口。请检查{}.{}", method.getDeclaringClass().getName(), method.getName()));
        }
        else
        {
            str += "\t" + paramType.getName() + " collection = new " + paramType.getName() + "();\n";
        }
        str += "\tfor(int i=0;i<size;i++)\n";
        str += "\t{\n";
        if (((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0] instanceof Class)
        {
            Class<?> nestParamType = (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
            if (wrapperSet.contains(nestParamType))
            {
                str += "\t\tcollection.add(jsonArray.getW" + nestParamType.getSimpleName() + "(i));\n";
            }
            else
            {
                str += "\t\tcollection.add(ReaderContext.read(" + nestParamType.getName() + ".class,jsonArray.get(i)));\n";
            }
        }
        else
        {
            str += "\t\tcollection.add(jsonArray.get(i));\n";
        }
        str += "\t}\n";
        str += "\t" + entityName + method.getName() + "(collection);\n";
        str += "}\n";
        
    }
    
}
