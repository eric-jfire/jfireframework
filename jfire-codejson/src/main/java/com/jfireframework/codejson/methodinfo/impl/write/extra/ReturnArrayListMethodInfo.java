package com.jfireframework.codejson.methodinfo.impl.write.extra;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.methodinfo.impl.write.AbstractWriteMethodInfo;
import com.jfireframework.codejson.tracker.Tracker;
import com.jfireframework.codejson.util.NameTool;

public class ReturnArrayListMethodInfo extends AbstractWriteMethodInfo
{
    
    public ReturnArrayListMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = "java.util.ArrayList " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy == null)
        {
            str += "\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
            str += "\tint size = " + fieldName + ".size();\n";
            str += "\tObject valueTmp = null;\n";
            str += "\tfor(int i=0;i<size;i++)\n";
            str += "\t{\n";
            str += "\t\tif((valueTmp=" + fieldName + ".get(i))!=null)\n";
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
        else
        {
            if (strategy.containsStrategyField(key))
            {
                str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
                str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
                if (strategy.isUseTracker())
                {
                    str += "\tif(_$tracker.getPath(" + fieldName + ")==null)\n";
                    str += "\t{\n";
                    str += "\t\t_$tracker.reset(" + entityName + ");\n";
                    str += "\t\tString newPath = _$tracker.getPath(" + entityName + ")+'.'+\"" + fieldName + "\";\n";
                    str += "\t\t_$tracker.put(" + fieldName + ",newPath);\n";
                    str += "\t}\n";
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
                    str += "\tif(_$tracker.getPath(" + fieldName + ")!=null)\n";
                    str += "\t{\n";
                    str += "\t\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
                    str += "\t\tif(writeStrategy.containsTrackerType(java.util.ArrayList.class))\n";
                    str += "\t\t{\n";
                    str += "\t\t\twriteStrategy.getTrackerType(java.util.ArrayList.class).write("+fieldName+",cache," + entityName + ",(Tracker)$4);\n";
                    str += "\t\t}\n";
                    str += "\t\telse\n";
                    str += "\t\t{\n";
                    str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(" + fieldName + ")).append('\"').append('}');\n";
                    str += "\t\t}\n";
                    str += "\t}\n";
                    str += "\telse\n";
                    str += "\t{\n";
                    str += "\t\t_$tracker.reset(" + entityName + ");\n";
                    str += "\t\tString newPath = _$tracker.getPath(" + entityName + ")+'.'+\"" + fieldName + "\";\n";
                    str += "\t\t_$tracker.put(" + fieldName + ",newPath);\n";
                    str += "\t\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
                    str += "\t\tint size = " + fieldName + ".size();\n";
                    str += "\t\tObject valueTmp = null;\n";
                    str += "\t\tfor(int i=0;i<size;i++)\n";
                    str += "\t\t{\n";
                    str += "\t\t\tif((valueTmp=" + fieldName + ".get(i))!=null)\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\tif(valueTmp instanceof String || valueTmp instanceof Number || valueTmp instanceof Boolean)\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\t\t\t\tcache.append('\\\"').append(valueTmp).append('\\\"');\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\telse\n";
                    str += "\t\t\t\t{\n";
                    str += "\t\t\t\t\t_$tracker.reset(" + fieldName + ");\n";
                    str += "\t\t\t\t\tif(_$tracker.getPath(valueTmp)!=null)\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\tif(writeStrategy.containsTrackerType(valueTmp.getClass()))\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\twriteStrategy.getTrackerType(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(valueTmp)).append('\"').append('}');\n";
                    str += "\t\t\t\t\t\t}\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t\telse\n";
                    str += "\t\t\t\t\t{\n";
                    str += "\t\t\t\t\t\tString newPath1 = _$tracker.getPath(" + fieldName + ")+'['+i+']';\n";
                    str += "\t\t\t\t\t\t_$tracker.put(valueTmp,newPath1);\n";
                    str += "\t\t\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                    str += "\t\t\t\t\t}\n";
                    str += "\t\t\t\t}\n";
                    str += "\t\t\t\tcache.append(',');\n";
                    str += "\t\t\t}\n";
                    str += "\t\t}\n";
                    str += "\t}\n";
                    str += "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                    str += "\tcache.append(\"],\");\n";
                    str += "}\n";
                }
                else
                {
                    str += "\tcache.append(\"\\\"" + fieldName + "\\\":[\");\n";
                    str += "\tint size = " + fieldName + ".size();\n";
                    str += "\tObject valueTmp = null;\n";
                    str += "\tfor(int i=0;i<size;i++)\n";
                    str += "\t{\n";
                    str += "\t\tif((valueTmp=" + fieldName + ".get(i))!=null)\n";
                    str += "\t\t{\n";
                    str += "\t\t\tif(valueTmp instanceof String || valueTmp instanceof Number || valueTmp instanceof Boolean)\n";
                    str += "\t\t\t{\n";
                    str += "\t\t\t\tcache.append('\\\"').append(valueTmp).append('\\\"');\n";
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
    }
    
}
