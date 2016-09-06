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
        str = "" + returnType.getName() + " " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy == null)
        {
            str += "\tcache.append('\"').append(" + getValue + ".name()).append('\"').append(',');\n";
        }
        else
        {
            if (strategy.containsStrategyField(key))
            {
                str += "\twriteStrategy.getWriterByField(\"" + key + "\").write(" + getValue + ",cache," + entityName + ");\n";
                str += "\tcache.append(',');\n";
            }
            else if (strategy.containsStrategyType(returnType))
            {
                str += "\twriteStrategy.getWriter(" + returnType.getName() + ".class).write(" + getValue + ",cache," + entityName + ");\n";
                str += "\tcache.append(',');\n";
            }
            else
            {
                if (strategy.isWriteEnumName())
                {
                    str += "\tcache.append('\"').append(" + getValue + ".name()).append('\"').append(',');\n";
                }
                else
                {
                    str += "\tcache.append(" + getValue + ".ordinal()).append(',');\n";
                }
            }
        }
        str+="}\n";
    }
    
}
