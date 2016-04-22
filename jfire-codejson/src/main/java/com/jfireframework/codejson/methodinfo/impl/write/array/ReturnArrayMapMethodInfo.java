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
        if (strategy == null)
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
            str += bk + "\t\t\t\tWriterContext.write(entry.getKey(),cache);\n";
            str += bk + "\t\t\t}\n";
            str += bk + "\t\t\tif(entry.getValue() instanceof String)\n";
            str += bk + "\t\t\t{\n";
            str += bk + "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
            str += bk + "\t\t\t}\n";
            str += bk + "\t\t\telse\n";
            str += bk + "\t\t\t{\n";
            str += bk + "\t\t\t\tWriterContext.write(entry.getValue(),cache);\n";
            str += bk + "\t\t\t}\n";
            str += bk + "\t\t\tcache.append(',');\n";
            str += bk + "\t\t}\n";
            str += bk + "\t}\n";
            str += bk + "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
            str += bk + "\tcache.append(\"},\");\n";
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
                str += bk + "\t\tString newPathArray0 = _$tracker.getPath(array1)+'['+i1+']';\n";
                str += bk + "\t\t_$tracker.put(array1[i1],newPathArray0);\n";
                str += bk + "\t\tcache.append('{');\n";
                str += bk + "\t\tIterator it = ((Map)array1[i1]).entrySet().iterator();\n";
                str += bk + "\t\tjava.util.Map.Entry entry = null;\n";
                str += bk + "\t\twhile(it.hasNext())\n";
                str += bk + "\t\t{\n";
                str += bk + "\t\t\tentry = it.next();\n";
                str += bk + "\t\t\tif(entry.getKey()!=null && entry.getValue()!=null)\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\tif(entry.getKey() instanceof String)\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\tcache.append('\\\"').append((String)entry.getKey()).append(\"\\\":\");\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\telse\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\tif(entry.getValue() instanceof String)\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\telse\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t_$tracker.reset(array1[i1]);\n";
                str += bk + "\t\t\t\t\tif(_$tracker.getPath(entry.getValue())!=null)\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tif(writeStrategy.containsTrackerType(entry.getValue().getClass()))\n";
                str += bk + "\t\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\t\twriteStrategy.getTrackerType(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(entry.getValue())).append('\"').append('}');\n";
                str += bk + "\t\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tString newpathentry = _$tracker.getPath(array1[i1])+'.'+entry.getKey().toString();\n";
                str += bk + "\t\t\t\t\t\t_$tracker.put(entry.getValue(),newpathentry);\n";
                str += bk + "\t\t\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\tcache.append(',');\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t}\n";
                str += bk + "\t\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                str += bk + "\t\tcache.append(\"},\");\n";
                str += bk + "\t}\n";
                str += bk + "}\n";
            }
            else
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
                str += bk + "\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ",null);\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\tif(entry.getValue() instanceof String)\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\telse\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",null);\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\tcache.append(',');\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
                str += bk + "cache.append(\"},\");\n";
                str += bk + "}\n";
            }
        }
    }
    
}
