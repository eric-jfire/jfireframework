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
            if (strategy.isUseTracker())
            {
                str += bk + "\t((Tracker)$4).reset(array1);\n";
                str += bk + "\tString patharray0 = ((Tracker)$4).getPath(array1[i1]);\n";
                str += bk + "\tif(patharray0!=null)\n";
                str += bk + "\t{\n";
                str += bk + "\t\tif(writeStrategy.containsTrackerType(array1[i1].getClass()))\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\twriteStrategy.getTrackerType(array1[i1].getClass()).write(array1[i1],cache," + entityName + ",(Tracker)$4);\n";
                str += bk + "\t\t}\n";
                str += bk + "\t\telse\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(patharray0).append('\"').append('}');\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "\telse\n";
                str += bk + "\t{\n";
                str += bk + "\t\tpatharray0 = ((Tracker)$4).getPath(array1)+\"[\"+i1+']';\n";
                str += bk + "\t\t((Tracker)$4).put(array1[i1],patharray0);\n";
                str += bk + "\t\twriteStrategy.getWriter(array1[i1].getClass()).write(array1[i1],cache," + entityName + ",(Tracker)$4);\n";
                str += bk + "\t}\n";
            }
            else
            {
                str += bk + "\twriteStrategy.getWriter(array1[i1].getClass()).write(array1[i1],cache," + entityName + ",null);\n";
            }
        }
        else
        {
            str += bk + '\t' + "WriterContext.write(array1[i1],cache);\n";
        }
        str += bk + '\t' + "cache.append(',');\n";
        str += bk + "}\n";
    }
}
