package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.util.NameTool;

public class ReturnEnumWriteMethodInfo extends AbstractWriteMethodInfo
{
    
    public ReturnEnumWriteMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        Class<?> returnType = method.getReturnType();
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "cache.append(\"\\\"" + fieldName + "\\\":\");\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy == null)
        {
            str += "cache.append('\"').append(" + getValue + ".name()).append('\"').append(',');\n";
        }
        else
        {
            if (strategy.containsStrategyField(key))
            {
                str += "writeStrategy.getWriterByField(\"" + key + "\").write(" + getValue + ",cache," + entityName + ");\n";
                str += "cache.append(',');\n";
            }
            else if (strategy.containsStrategyType(returnType))
            {
                str += "writeStrategy.getWriter(" + returnType.getName() + ".class).write(" + getValue + ",cache," + entityName + ");\n";
                str += "cache.append(',');\n";
            }
            else
            {
                if (strategy.isWriteEnumName())
                {
                    str += "cache.append('\"').append(" + getValue + ".name()).append('\"').append(',');\n";
                }
                else
                {
                    str += "cache.append(" + getValue + ".ordinal()).append(',');\n";
                }
            }
        }
    }
    
}
