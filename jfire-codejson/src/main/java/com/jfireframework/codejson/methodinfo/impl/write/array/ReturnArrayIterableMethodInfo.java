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
        if (strategy == null)
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
            str += bk + "\t\t\t\tWriterContext.write(valueTmp,cache);\n";
            str += bk + "\t\t\t}\n";
            str += bk + "\t\t\tcache.append(',');\n";
            str += bk + "\t\t}\n";
            str += bk + "\t}\n";
            str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
            str += bk + "cache.append(\"],\");\n";
            str += bk + "}\n";
        }
        else
        {
            if (strategy.isUseTracker())
            {
                str += bk + "if(array1[i1]!=null)\n";
                str += bk + "{\n";
                str += bk + "\t_$tracker.reset(array1);\n";
                str += bk + "\tif(_$tracker.getPath(array1[i1])!=null)\n";
                str += bk + "\t{\n";
                str += bk + "\t\tif(writeStrategy.containsTrackerType(array1[i1].getClass()))\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\twriteStrategy.getTrackerType(array1[i1].getClass()).write(array1[i1],cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t}\n";
                str += bk + "\t\telse\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(array1[i1])).append('\"').append('}');\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "\telse\n";
                str += bk + "\t{\n";
                str += bk + "\t\tString newpathArray0 = _$tracker.getPath(array1)+\"[\"+i1+']';\n";
                str += bk + "\t\t_$tracker.put(array1[i1],newpathArray0);\n";
                str += bk + "\t\tcache.append('[');\n";
                str += bk + "\t\tObject valueTmp = null;\n";
                str += bk + "\t\tIterator it = ((Iterable)array1[i1]).iterator();\n";
                str += bk + "\t\tint count = 0;\n";
                str += bk + "\t\twhile(it.hasNext())\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\tif((valueTmp=it.next())!=null)\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\tif(valueTmp instanceof String)\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\tcache.append('\\\"').append((String)valueTmp).append('\\\"');\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\telse\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t_$tracker.reset(array1[i1]);\n";
                str += bk + "\t\t\t\t\tif(_$tracker.getPath(valueTmp)!=null)\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tif(writeStrategy.containsTrackerType(valueTmp.getClass()))\n";
                str += bk + "\t\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\t\twriteStrategy.getTrackerType(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(valueTmp)).append('\"').append('}');\n";
                str += bk + "\t\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tString newPatharray1 = _$tracker.getPath(array1[i1])+'['+count+']';\n";
                str += bk + "\t\t\t\t\t\t_$tracker.put(valueTmp,newPatharray1);\n";
                str += bk + "\t\t\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\tcache.append(',');\n";
                str += bk + "\t\t\t\tcount+=1;\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                str += bk + "\tcache.append(\"],\");\n";
                str += bk + "}\n";
            }
            else
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
                str += bk + "\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",null);\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\tcache.append(',');\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
                str += bk + "cache.append(\"],\");\n";
                str += bk + "}\n";
            }
            
        }
    }
    
}
