package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;

public class ReturnArrayCustomObjectMethodInfo extends AbstractWriteArrayMethodInfo
{
    
    public ReturnArrayCustomObjectMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
    }
    
    @Override
    protected void writeOneDim(Class<?> rootType, String bk)
    {
        str += bk + "if(array1[i1]!=null)\n";
        str += bk + "{\n";
        if (strategy != null)
        {
            str += bk + "\twriteStrategy.getWriter(array1[i1].getClass()).write(array1[i1],cache," + entityName + ");\n";
        }
        else
        {
            str += bk + '\t' + "WriterContext.write(array1[i1],cache);\n";
        }
        str += bk + '\t' + "cache.append(',');\n";
        str += bk + "}\n";
    }
}
