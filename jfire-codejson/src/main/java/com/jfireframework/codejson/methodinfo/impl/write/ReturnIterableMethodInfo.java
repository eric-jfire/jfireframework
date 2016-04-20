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
            str += "\tString newPath = ((Tracker)$4).getPath(" + entityName + ")+\"." + fieldName + "\";\n";
            str += "\t((Tracker)$4).put(" + fieldName + ",newPath);\n";
            str += "\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
            str += "\tcache.append(',');\n";
            str += "}\n";
        }
        else
        {
            if (strategy != null && strategy.isUseTracker())
            {
                str += "\tString path = ((Tracker)$4).getPath(" + fieldName + ");\n";
                str += "\tif(path != null)\n";
                str += "\t{\n";
                str += "\t\tif(writeStrategy.containsTrackerType(" + fieldName + ".getClass()))\n";
                str += "\t\t{\n";
                str += "\t\t\twriteStrategy.getTrackerType(" + fieldName + ".getClass()).write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
                str += "\t\t}\n";
                str += "\t\telse\n";
                str += "\t\t{\n";
                str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(path).append('\"').append('}');\n";
                str += "\t\t}\n";
                str += "\telse\n";
                str += "\t{\n";
                str += "\t\tString newPath = ((Tracker)$4).getPath(" + entityName + ")+'.\"" + fieldName + "\";\n";
                str += "\t\t((Tracker)$4).put(" + fieldName + ",newPath);\n";
            }
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
            str += "\tint count = 0;\n";
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
            str += "\t\t\t\tcount+=1;\n";
            str += "\t\t\t}\n";
            str += "\t\t\telse\n";
            str += "\t\t\t{\n";
            if (strategy != null)
            {
                if (strategy.isUseTracker())
                {
                    str += "\t\t\t\tString path1 = ((Tracker)$4).getPath(valueTmp);\n";
                    str += "\t\t\t\tif(path1 != null)\n\t{\n";
                    str += "\t\t\t\t\tif(writeStrategy.containsTrackerType(valueTmp.getClass()))\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\twriteStrategy.getTrackerType(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",(Tracker)$4);\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(path1).append('\"').append('}');\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\telse\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\tString newPath1 = ((Tracker)$4).getPath(" + entityName + ")+'.\"" + fieldName + "[\"+count+\"]\";\n";
                    str += "\t\t\t\t\t((Tracker)$4).put(valueTmp,newPath1);\n";
                    str += "\t\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",(Tracker)$4);\n";
                    str += "\t\t\t\t}\n";
                }
                else
                {
                    str += "\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",null);\n";
                }
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
