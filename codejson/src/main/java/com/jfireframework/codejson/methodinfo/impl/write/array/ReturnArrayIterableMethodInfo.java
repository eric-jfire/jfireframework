package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;

public class ReturnArrayIterableMethodInfo extends AbstractWriteArrayMethodInfo
{
    
    public ReturnArrayIterableMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
    }
    
    @Override
    protected void writeOneDim(Class<?> rootType, String bk)
    {
        str += bk + "if(array1[i1]!=null)\n";
        str += bk + "{\n";
        str += bk + "\tcache.append('[');\n";
        str += bk + "\tObject valueTmp = null;\n";
        str += bk + "\tIterator it = ((Iterable)array1[i1]).iterator();\n";
        str += bk + "\twhile(it.hasNext())\n";
        str += bk + "\t{\n";
        str += bk + "\t\tif((valueTmp=it.next())!=null)\n";
        str += bk + "\t\t{\n";
        str += bk + "\t\t\tif(valueTmp instanceof String)\n";
        str += bk + "\t\t\t{\n";
        str += bk + "\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\telse\n";
        str += bk + "\t\t\t{\n";
        if (strategy != null)
        {
            str += bk + "\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ");\n";
        }
        else
        {
            str += bk + "\t\t\t\tWriterContext.write(valueTmp,cache);\n";
        }
        str += bk + "\t\t\t}\n";
        str += bk + "\t\t\tcache.append(',');\n";
        str += bk + "\t\t}\n";
        str += bk + "\t}\n";
        str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
        str += bk + "cache.append(\"],\");\n";
        str += bk + "}\n";
    }
    
}
