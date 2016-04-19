package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.util.NameTool;

public class ReturnCustomObjectMethodInfo extends AbstractWriteMethodInfo
{
    
    public ReturnCustomObjectMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        Class<?> returnType = method.getReturnType();
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str = returnType.getName() + " " + fieldName + " = " + getValue + ";\n";
        str += "if(" + fieldName + "!=null)\n{\n";
        str += "\tcache.append(\"\\\"" + fieldName + "\\\":\");\n";
        String key = method.getDeclaringClass().getName() + '.' + fieldName;
        if (strategy != null)
        {
            if (strategy.containsStrategyField(key))
            {
                str += "\tJsonWriter writer = writeStrategy.getWriterByField(\"" + key + "\");\n";
            }
            else
            {
                str += "\tJsonWriter writer = writeStrategy.getWriter(" + fieldName + ".getClass());\n";
            }
            if (strategy.isUseTracker())
            {
                str += "\tString path = ((Tracker)$4).getPath(" + fieldName + ");\n";
                str += "\tif(path != null)\n\t{\n";
                str += "\t\tif(writeStrategy.containsTrackerType(" + fieldName + ".getClass()))\n";
                str += "\t\t{\n";
                str += "\t\t\twriter = writeStrategy.getTrackerType(" + fieldName + ".getClass());\n";
                str += "\t\t\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
                str += "\t\t}\n";
                str += "\t\telse\n";
                str += "\t\t{\n";
                str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(path).append('\"').append('}');\n";
                str += "\t\t}\n";
                str += "\t}\n";
                str += "\telse\n";
                str += "\t{\n";
                str += "\t\tString newPath = ((Tracker)$4).getPath(" + entityName + ")+'.'+" + fieldName + ";\n";
                str += "\t\t((Tracker)$4).put(" + fieldName + ",newPath);\n";
                str += "\t\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
                str += "\t}\n";
            }
            else
            {
                str += "\twriter.write(" + fieldName + ",cache," + entityName + ",(Tracker)$4);\n";
            }
        }
        else
        {
            str += "\tWriterContext.write(" + fieldName + ",cache);\n";
        }
        str += "\tcache.append(',');\n";
        str += "}\n";
    }
    
}
