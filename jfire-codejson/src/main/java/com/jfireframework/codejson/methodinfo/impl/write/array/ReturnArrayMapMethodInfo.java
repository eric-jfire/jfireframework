package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;

public class ReturnArrayMapMethodInfo extends AbstractWriteArrayMethodInfo
{
    
    public ReturnArrayMapMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
    }
    
    @Override
    protected void writeOneDim(Class<?> rootType, String bk)
    {
        str += bk + "if(array1[i1]!=null)\n";
        str += bk + "{\n";
        str += bk + "\tcache.append('{');\n";
        str += bk + "\tIterator it = ((Map)array1[i1]).entrySet().iterator();\n";
        str += bk + "\tjava.util.Map.Entry entry = null;\n";
        str += bk + "\twhile(it.hasNext())\n";
        str += bk + "\t{\n";
        str += bk + "\t\tentry = it.next();\n";
        str += bk + "\t\tif(entry.getKey()!=null && entry.getValue()!=null)\n";
        str += bk + "\t\t{\n";
        str += bk + "\t\t\tif(entry.getKey() instanceof String)\n";
        str += bk + "\t\t\t{\n";
        str += bk + "\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\telse\n";
        str += bk + "\t\t\t{\n";
        if (strategy != null)
        {
            str += bk + "\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ");\n";
        }
        else
        {
            str += bk + "\t\t\t\tWriterContext.write(entry.getKey(),cache);\n";
        }
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\tif(entry.getValue() instanceof String)\n";
        str += bk + "\t\t\t{\n";
        str += bk + "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\telse\n";
        str += bk + "\t\t\t{\n";
        if (strategy != null)
        {
            str += bk + "\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ");\n";
        }
        else
        {
            str += bk + "\t\t\t\tWriterContext.write(entry.getValue(),cache);\n";
        }
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\tcache.append(',');\n";
        str += bk + "\t\t}\n";
        str += bk + "\t}\n";
        str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
        str += bk + "cache.append(\"},\");\n";
        str += bk + "}\n";
    }
    
}
