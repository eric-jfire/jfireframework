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
        if (strategy != null)
        {
            if (strategy.containsStrategyField(key))
            {
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
                str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\tint _$index = _$tracker.indexOf(" + fieldName + ");\n";
                    str += "\tif(_$index == -1)\n";
                    str += "\t{\n";
                    str += "\t\t_$tracker.put(" + fieldName + ",\"" + fieldName + "\",false);\n";
                    str += "\t}\n";
                    str += "\telse\n";
                    str += "\t{\n";
                    str += "\t\twriter.write(" + fieldName + ",cache," + entityName + ",_$tracker);\n";
                    str += "\t}\n";
                    str += "}\n";
                }
                else
                {
                    str += "\twriter.write(" + fieldName + ",cache," + entityName + ",null);\n";
                    str += "\tcache.append(',');\n";
                    str += "}\n";
                }
            }
            else
            {
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\tint _$index = _$tracker.indexOf(" + fieldName + ");\n";
                    str += "\tif(_$index != -1)\n\t{\n";
                    str += "\t\tJsonWriter writer = writeStrategy.getTrackerType(" + fieldName + ".getClass());\n";
                    str += "\t\tif(writer != null)\n";
                    str += "\t\t{\n";
                    str += "\t\t\twriter.write(" + fieldName + ",cache," + entityName + ",_$tracker);\n";
                    str += "\t\t}\n";
                    str += "\t\telse\n";
                    str += "\t\t{\n";
                    str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$index)).append('\"').append('}');\n";
                    str += "\t\t}\n";
                    str += "\t}\n";
                    str += "\telse\n";
                    str += "\t{\n";
                    str += "\t\tint _$reIndex1 = _$tracker.put(" + fieldName + ",\"" + fieldName + "\",false);\n";
                    str += "\t\tcache.append(\"\\\"" + fieldName + "\\\":{\");\n";
                    str += "\t\tSet entries = " + fieldName + ".entrySet();\n";
                    str += "\t\tIterator it = entries.iterator();\n";
                    str += "\t\tjava.util.Map.Entry entry = null;\n";
                    str += "\t\twhile(it.hasNext())\n";
                    str += "\t\t{\n";
                    str += "\t\t\tentry = it.next();\n";
                    str += "\t\t\tif(entry.getKey()!=null && entry.getValue()!=null)\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\tif(entry.getKey() instanceof String)\n";
                    str += "\t\t\t\t{\n";
                    if (strategy.getWriter(String.class) instanceof StringWriter)
                    {
                        str += "\t\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
                    }
                    else
                    {
                        str += "\t\t\t\t\twriteStrategy.getWriter(String.class).write(entry.getKey(),cache," + entityName + ",_$tracker);\n";
                        str += "\t\t\t\t\tcache.append(':');\n";
                    }
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\telse\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\t\t\t\tcache.append('\"');\n";
                    str += "\t\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\tcache.append(\"\\\":\");\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\tif(entry.getValue() instanceof String)\n";
                    str += "\t\t\t\t{\n";
                    if (strategy.getWriter(String.class) instanceof StringWriter == false)
                    {
                        str += "\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                    }
                    else
                    {
                        str += "\t\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                    }
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\telse\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\t\t\t\t_$tracker.reset(_$reIndex1);\n";
                    str += "\t\t\t\t\tint _$index1 = _$tracker.indexOf(entry.getValue());\n";
                    str += "\t\t\t\t\tif(_$index1 != -1)\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t JsonWriter writer = writeStrategy.getTrackerType(entry.getValue().getClass());\n";
                    str += "\t\t\t\t\t\tif(writer != null)\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\twriter.write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$index1)).append('\"').append('}');\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t_$tracker.put(entry.getValue(),entry.getKey().toString(),false);\n";
                    str += "\t\t\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\tcache.append(',');\n";
                    str += "\t\t\t}\n";
                    str += "\t\t}\n";
                    str += "\t\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                    str += "\t\tcache.append(\"},\");\n";
                    str += "\t}\n";
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
                    if (strategy.getWriter(String.class) instanceof StringWriter)
                    {
                        str += "\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
                    }
                    else
                    {
                        str += "\t\t\t\twriteStrategy.getWriter(String.class).write(entry.getKey(),cache," + entityName + ",null);\n";
                        str += "\t\t\t\tcache.append(':');\n";
                    }
                    str += "\t\t\t}\n";
                    str += "\t\t\telse\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\tcache.append('\"');\n";
                    str += "\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\tcache.append(\"\\\":\");\n";
                    str += "\t\t\t}\n";
                    if (strategy.getWriter(String.class) instanceof StringWriter == false)
                    {
                        str += "\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",null);\n";
                    }
                    else
                    {
                        str += "\t\t\tif(entry.getValue() instanceof String)\n";
                        str += "\t\t\t{\n";
                        str += "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                        str += "\t\t\t}\n";
                        str += "\t\t\telse\n";
                        str += "\t\t\t{\n";
                        str += "\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",null);\n";
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
            str += "\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            str += "\t\t\t\tcache.append('\"');\n";
            str += "\t\t\t\tWriterContext.write(entry.getKey(),cache);\n";
            str += "\t\t\t\tcache.append(\"\\\":\");\n";
            str += "\t\t\t}\n";
            str += "\t\t\tif(entry.getValue() instanceof String)\n";
            str += "\t\t\t{\n";
            str += "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            str += "\t\t\t\tWriterContext.write(entry.getValue(),cache);\n";
            str += "\t\t\t}\n";
            str += "\t\t\tcache.append(',');\n";
            str += "\t\t}\n";
            str += "\t}\n";
            str += "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
            str += "\tcache.append(\"},\");\n";
            str += "}\n";
        }
    }
}
