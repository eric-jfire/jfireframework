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
        if (strategy != null)
        {
            if (strategy.containsStrategyField(key))
            {
                // 由于在这里不能确定到底序列化的内容是什么，所以只输出到:号为止
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
                str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\t_$tracker.put(" + fieldName + ",\"" + fieldName + "\",false);\n";
                    str += "\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
                }
                else
                {
                    str += "\twriter.write(" + fieldName + ",cache," + entityName + ",null);\n";
                }
                str += "\tcache.append(',');\n";
                str += "}\n";
            }
            else
            {
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\tint _$index = _$tracker.indexOf(" + fieldName + ");\n";
                    str += "\tif(_$index != -1)\n";
                    str += "\t{\n";
                    str += "\t\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
                    str += "\t\tJsonWriter writer = writeStrategy.getTrackerType(" + fieldName + ".getClass());\n";
                    str += "\t\tif(writer != null)\n";
                    str += "\t\t{\n";
                    str += "\t\t\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
                    str += "\t\t}\n";
                    str += "\t\telse\n";
                    str += "\t\t{\n";
                    str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$index)).append('\"').append('}');\n";
                    str += "\t\t}\n";
                    str += "\telse\n";
                    str += "\t{\n";
                    str += "\t\tint _$reIndex1 = _$tracker.put(" + fieldName + ",\"" + fieldName + "\",false);\n";
                    str += "\t\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
                    str += "\t\tint count = 0;\n";
                    str += "\t\tIterator it =" + fieldName + ".iterator();\n";
                    str += "\t\tObject valueTmp = null;\n";
                    str += "\t\twhile(it.hasNext())\n";
                    str += "\t\t{\n";
                    str += "\t\t\tif((valueTmp=it.next())!=null)\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\tif(valueTmp instanceof String || valueTmp instanceof Number || valueTmp instanceof Boolean)\n";
                    str += "\t\t\t\t{\n";
                    if (strategy.getWriter(String.class) instanceof StringWriter)
                    {
                        str += "\t\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
                    }
                    else
                    {
                        str += "\t\t\t\t\twriteStrategy.getWriter(String.class).write((String)valueTmp,cache," + entityName + ",_$tracker);\n";
                    }
                    str += "\t\t\t\t\tcount+=1;\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\telse\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\t\t\t\t_$tracker.reset(_$reIndex1);\n";
                    str += "\t\t\t\t\tint _$index1 = _$tracker.indexOf(valueTmp);\n";
                    str += "\t\t\t\t\tif(_$index1 != -1)\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\tJsonWriter writer = writeStrategy.getTrackerType(valueTmp.getClass());\n";
                    str += "\t\t\t\t\t\tif(writer != null)\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\twriter.write(valueTmp,cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$index1)).append('\"').append('}');\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t\tcount+=1;\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t_$tracker.put(valueTmp,\"[\"+count+\"]\",true);\n";
                    str += "\t\t\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t\tcount+=1;\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t}\n";
                    str += "\t\t}\n";
                    str += "\t\tcache.append(',');\n";
                    str += "\t}\n";
                    str += "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                    str += "\tcache.append(\"],\");\n";
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
                    if (strategy.getWriter(String.class) instanceof StringWriter)
                    {
                        str += "\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
                    }
                    else
                    {
                        str += "\t\t\t\twriteStrategy.getWriter(String.class).write((String)valueTmp,cache," + entityName + ",null);\n";
                    }
                    str += "\t\t\t}\n";
                    str += "\t\t\telse\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",null);\n";
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
            str += "\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            str += "\t\t\t\tWriterContext.write(valueTmp,cache);\n";
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
