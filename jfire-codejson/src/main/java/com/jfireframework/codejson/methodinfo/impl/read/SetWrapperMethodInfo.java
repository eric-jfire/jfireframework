package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetWrapperMethodInfo extends AbstractReadMethodInfo
{
    
    public SetWrapperMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        String jsonGetMethodName = "getW" + getParamType().getSimpleName().substring(0, 1).toUpperCase() + getParamType().getSimpleName().substring(1);
        str="";
        if (strategy != null && (strategy.containsStrategyField(strategyFieldName) || strategy.containsStrategyType(getParamType())))
        {
            if (strategy.containsStrategyField(strategyFieldName))
            {
                str +=  entityName + method.getName() + "((" + getParamType().getName() + ")readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\")));\n";
            }
            else
            {
                str +=  entityName + method.getName() + "((" + getParamType().getName() + ")readStrategy.getReader(" + getParamType().getName() + ".class).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\")));\n";
            }
        }
        else
        {
            str += entityName + method.getName() + "(json." + jsonGetMethodName + "(\"" + fieldName + "\"));\n";
        }
    }
    
}
