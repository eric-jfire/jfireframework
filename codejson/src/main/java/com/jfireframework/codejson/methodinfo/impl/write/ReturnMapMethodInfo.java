package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.wrapper.StringWriter;
import com.jfireframework.codejson.util.NameTool;

public class ReturnMapMethodInfo extends AbstractWriteMethodInfo
{
    
    public ReturnMapMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "Map " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy != null && strategy.containsStrategyField(key))
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
            str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
            str += "\twriter.write(" + fieldName + ",cache,"+entityName+");\n";
            str += "\tcache.append(',');\n";
            str += "}\n";
        }
        else
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":{\");\n";
            str += "\tSet entries = " + fieldName + ".entrySet();\n";
            str += "\tIterator it = entries.iterator();\n";
            str += "\tjava.util.Map.Entry entry = null;\n";
            str += "\twhile(it.hasNext())\n\t{\n";
            str += "\t\tentry = it.next();\n";
            str += "\t\tif(entry.getKey()!=null && entry.getValue()!=null)\n";
            str += "\t\t{\n";
            str += "\t\t\tif(entry.getKey() instanceof String)\n";
            str += "\t\t\t{\n";
            if (strategy != null && (strategy.getWriter(String.class) instanceof StringWriter == false))
            {
                str += "\t\t\t\twriteStrategy.getWriter(String.class).write(entry.getKey(),cache,"+entityName+");\n";
                str += "\t\t\t\tcache.append(':');\n";
            }
            else
            {
                str += "\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
            }
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            str += "\t\t\t\tcache.append('\"');\n";
            if (strategy != null)
            {
                str += "\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache,"+entityName+");\n";
            }
            else
            {
                str += "\t\t\t\tWriterContext.write(entry.getKey(),cache);\n";
            }
            str += "\t\t\t\tcache.append(\"\\\":\");\n";
            str += "\t\t\t}\n";
            if (strategy != null && (strategy.getWriter(String.class) instanceof StringWriter == false))
            {
                str += "\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache,"+entityName+");\n";
            }
            else
            {
                str += "\t\t\tif(entry.getValue() instanceof String)\n";
                str += "\t\t\t{\n";
                str += "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                str += "\t\t\t}\n";
                str += "\t\t\telse\n";
                str += "\t\t\t{\n";
                if (strategy != null)
                {
                    str += "\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache,"+entityName+");\n";
                }
                else
                {
                    str += "\t\t\t\tWriterContext.write(entry.getValue(),cache);\n";
                }
                str += "\t\t\t}\n";
            }
            str += "\t\t\tcache.append(',');\n";
            str += "\t\t}\n";
            str += "\t}\n";
            str += "if(cache.isCommaLast()){cache.deleteLast();}\n";
            str += "cache.append(\"},\");\n";
            str += "}\n";
        }
    }
}
