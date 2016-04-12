package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.wrapper.StringWriter;
import com.jfireframework.codejson.util.NameTool;

/**
 * 如果方法的返回对象实现了Iterable接口，则使用该方法返回热编译代码
 * 
 */
public class ReturnIterableMethodInfo extends AbstractWriteMethodInfo
{
    public ReturnIterableMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "Iterable " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy != null && strategy.containsStrategyField(key))
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
            str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
            str += "\twriter.write(" + fieldName + ",cache," + entityName + ");\n";
            str += "\tcache.append(',');\n";
            str += "}\n";
        }
        else
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
            str += "\tIterator it =" + fieldName + ".iterator();\n";
            str += "\tObject valueTmp = null;\n";
            str += "\twhile(it.hasNext())\n\t{\n";
            str += "\t\tif((valueTmp=it.next())!=null)\n";
            str += "\t\t{\n";
            str += "\t\t\tif(valueTmp instanceof String)\n";
            str += "\t\t\t{\n";
            if (strategy != null && (strategy.getWriter(String.class) instanceof StringWriter == false))
            {
                str += "\t\t\t\twriteStrategy.getWriter(String.class).write((String)valueTmp,cache," + entityName + ");\n";
            }
            else
            {
                str += "\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
            }
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            if (strategy != null)
            {
                str += "\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache,"+entityName+");\n";
            }
            else
            {
                str += "\t\t\t\tWriterContext.write(valueTmp,cache);\n";
            }
            str += "\t\t\t}\n";
            str += "\t\t\tcache.append(',');\n";
            str += "\t\t}\n";
            str += "\t}\n";
            str += "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
            str += "\tcache.append(\"],\");\n";
            str += "}\n";
        }
        
    }
    
}
