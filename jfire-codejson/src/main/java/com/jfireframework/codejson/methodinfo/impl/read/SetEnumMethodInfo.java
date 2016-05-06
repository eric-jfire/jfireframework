package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetEnumMethodInfo extends AbstractReadMethodInfo
{
    public SetEnumMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        str = "if(json.contains(\"" + fieldName + "\"))\n";
        str += "{\n";
        if (strategy == null)
        {
            str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")Enum.valueOf(" + getParamType().getName() + ".class,json.getWString(\"" + fieldName + "\")));\n";
        }
        else
        {
            if (strategy.containsStrategyField(strategyFieldName))
            {
                str += "\t" + getParamType().getName() + "value = readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")value);\n";
            }
            else if (strategy.containsStrategyType(getParamType()))
            {
                str += "\t" + getParamType().getName() + "value = readStrategy.getReader(" + getParamType() + ".class).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")value);\n";
            }
            else if (strategy.isReadEnumName())
            {
                str += entityName + method.getName() + "((" + getParamType().getName() + ")Enum.valueOf(" + getParamType().getName() + ".class,json.getWString(\"" + fieldName + "\")));\n";
            }
            else
            {
                str += entityName + method.getName() + "((" + getParamType().getName() + ")" + getParamType().getName() + ".values()[json.getInt(\"" + fieldName + "\")]);\n";
            }
        }
        str += "}\n";
    }
}
