package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.util.NameTool;

/**
 * 用于对基础类的处理
 * 
 * @author linbin
 * 
 */
public class ReturnBaseMethodInfo extends AbstractWriteMethodInfo
{
    
    public ReturnBaseMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        Class<?> returnType = method.getReturnType();
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "cache.append(\"\\\"" + fieldName + "\\\":\");\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy != null && (strategy.containsStrategyType(returnType) || strategy.containsStrategyField(key)))
        {
            if (strategy.containsStrategyField(key))
            {
                str += "writeStrategy.getWriterByField(\"" + key + "\").write(" + getValue + ",cache," + entityName + ");\n";
            }
            else
            {
                str += "writeStrategy.getWriter(" + returnType.getName() + ".class).write(" + getValue + ",cache," + entityName + ");\n";
            }
            str += "cache.append(',');\n";
        }
        else
        {
            if (returnType.equals(char.class))
            {
                str += "cache.append('\"').append(" + getValue + ").append('\"').append(',');\n";
            }
            else
            {
                str += "cache.append(" + getValue + ").append(',');\n";
            }
        }
    }
}
