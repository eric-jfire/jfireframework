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
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\tint _$index = _$tracker.indexOf(" + fieldName + ");\n";
                    str += "\tif(_$index == -1)\n";
                    str += "\t{\n";
                    str += "\t\t_$tracker.put(" + fieldName + ",\""+fieldName+"\",false);\n";
                    str += "\t}\n";
                    str += "\twriter.write(" + fieldName + ",cache," + entityName + ",_$tracker);\n";
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
                str += "\tJsonWriter writer = writeStrategy.getWriter(" + fieldName + ".getClass());\n";
                if (strategy.isUseTracker())
                {
                    str += "\t_$tracker.reset(_$reIndex);\n";
                    str += "\tint _$index = _$tracker.indexOf(" + fieldName + ");\n";
                    str += "\tif(_$index != -1)\n\t{\n";
                    str += "\t\twriter = writeStrategy.getTrackerType(" + fieldName + ".getClass());\n";
                    str += "\t\tif(writer != null )\n";
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
                    str += "\t\t_$tracker.put(" + fieldName + ",\""+fieldName+"\",false);\n";
                    str += "\t\twriter.write(" + fieldName + ",cache," + entityName + ",_$tracker);\n";
                    str += "\t}\n";
                }
                else
                {
                    str += "\twriter.write(" + fieldName + ",cache," + entityName + ",null);\n";
                }
                str += "\tcache.append(',');\n";
                str += "}\n";
            }
        }
        else
        {
            str += "\tWriterContext.write(" + fieldName + ",cache);\n";
            str += "\tcache.append(',');\n";
            str += "}\n";
        }
    }
    
}
