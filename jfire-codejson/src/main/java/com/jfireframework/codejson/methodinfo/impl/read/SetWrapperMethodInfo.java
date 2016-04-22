package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetWrapperMethodInfo extends AbstractReadMethodInfo
{
    
    public SetWrapperMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        String jsonGetMethodName = "getW" + getParamType().getSimpleName().substring(0, 1).toUpperCase() + getParamType().getSimpleName().substring(1);
        str = "if(json.contains(\"" + fieldName + "\"))\n";
        str += "{\n";
        if (strategy != null && (strategy.containsStrategyField(strategyFieldName) || strategy.containsStrategyType(getParamType())))
        {
            if (strategy.containsStrategyField(strategyFieldName))
            {
                str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\")));\n";
            }
            else
            {
                str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")readStrategy.getReader(" + getParamType().getName() + ".class).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\")));\n";
            }
        }
        else
        {
            str += "\t" + entityName + method.getName() + "(json." + jsonGetMethodName + "(\"" + fieldName + "\"));\n";
        }
        str += "}\n";
    }
    
}
