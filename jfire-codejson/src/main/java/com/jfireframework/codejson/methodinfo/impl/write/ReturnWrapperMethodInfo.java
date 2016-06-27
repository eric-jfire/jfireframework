package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.util.NameTool;

/**
 * 用于处理对基础类的包装类，同时也包含对String的处理
 * 
 * @author linbin
 * 
 */
public class ReturnWrapperMethodInfo extends AbstractWriteMethodInfo
{
    public ReturnWrapperMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        Class<?> returnType = method.getReturnType();
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "" + returnType.getSimpleName() + " " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy != null && (strategy.containsStrategyType(returnType) || strategy.containsStrategyField(key)))
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
            if (strategy.containsStrategyField(key))
            {
                str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
            }
            else
            {
                str += "\tJsonWriter writer = writeStrategy.getWriter(" + fieldName + ".getClass());\n";
            }
            str += "\twriter.write(" + fieldName + ",cache," + entityName + ",_$tracker);\n";
            str += "\tcache.append(',');\n";
            str += "}\n";
        }
        else
        {
            if (returnType == String.class)
            {
                str += "\tString value_tmp_1 = " + fieldName + ";\n";
//                str += "\tvalue_tmp_1 = value_tmp_1.replace(\"\\\\\",\"\\\\\\\\\");\n";
//                str += "\tvalue_tmp_1 = value_tmp_1.replace(\"\\\"\",\"\\\\\\\"\");\n";
//                str += "\tvalue_tmp_1 = value_tmp_1.replace(\"\\r\",\"\\\\r\");\n";
//                str += "\tvalue_tmp_1 = value_tmp_1.replace(\"\\n\",\"\\\\n\");\n";
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\\\"\").append(value_tmp_1).append(\"\\\",\");\n";
            }
            else if (returnType == Character.class)
            {
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\\\"\").append(" + fieldName + ").append(\"\\\",\");\n";
            }
            else
            {
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\").append(" + fieldName + ").append(',');\n";
            }
            str += "}\n";
        }
    }
    
}
