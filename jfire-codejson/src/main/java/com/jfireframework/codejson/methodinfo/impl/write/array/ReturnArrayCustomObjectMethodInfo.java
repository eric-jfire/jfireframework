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
                str += bk + "\t_$tracker.reset(_$reIndexarray1);\n";
                str += bk + "\tint _$reIndexarray0 = _$tracker.indexOf(array1[i1]);\n";
                str += bk + "\tif(_$reIndexarray0 != -1)\n";
                str += bk + "\t{\n";
                str += bk + "\t\tJsonWriter writerarray0 = writeStrategy.getTrackerType(array1[i1].getClass());\n";
                str += bk + "\t\tif(writerarray0 != null)\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\twriterarray0.write(array1[i1],cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t}\n";
                str += bk + "\t\telse\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$reIndexarray0)).append('\"').append('}');\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "\telse\n";
                str += bk + "\t{\n";
                str += bk + "\t\t_$tracker.put(array1[i1],\"[\"+i1+']',true);\n";
                str += bk + "\t\twriteStrategy.getWriter(array1[i1].getClass()).write(array1[i1],cache," + entityName + ",_$tracker);\n";
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
