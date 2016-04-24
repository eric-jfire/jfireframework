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
                str += bk + "_$tracker.reset(_$reIndexarray1);\n";
                str += bk + "int _$reIndexarray0 = _$tracker.indexOf(array1[i1]);\n";
                str += bk + "if(_$reIndexarray0 != -1)\n";
                str += bk + "{\n";
                str += bk + "\tJsonWriter writerarray0 = writeStrategy.getTrackerType(array1[i1].getClass());\n";
                str += bk + "\tif(writerarray0 != null)\n";
                str += bk + "\t{\n";
                str += bk + "\t\twriterarray0.write(array1[i1],cache," + entityName + ",_$tracker);\n";
                str += bk + "\t}\n";
                str += bk + "\telse\n";
                str += bk + "\t{\n";
                str += bk + "\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$reIndexarray0)).append('\"').append('}');\n";
                str += bk + "\t}\n";
                str += bk + "}\n";
                str += bk + "else\n";
                str += bk + "{\n";
                str += bk + "\t_$reIndexarray0 = _$tracker.put(array1[i1],\"[\"+i1+']',true);\n";
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
                str += bk + "\t\t\t\twriteStrategy.getWriter(entry.getKey().getClass()).write(entry.getKey(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\tif(entry.getValue() instanceof String)\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\tcache.append('\\\"').append((String)entry.getValue()).append('\\\"');\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\telse\n";
                str += bk + "\t\t\t{\n";
                str += bk + "\t\t\t\t_$tracker.reset(_$reIndexarray0);\n";
                str += bk + "\t\t\t\tint _$indexarray0 = _$tracker.indexOf(entry.getValue());\n\n";
                str += bk + "\t\t\t\tif(_$indexarray0 != -1)\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\tJsonWriter writerarray0_1 = writeStrategy.getTrackerType(entry.getValue().getClass());\n";
                str += bk + "\t\t\t\t\tif(writerarray0_1 != null)\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\twriterarray0_1.write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$indexarray0)).append('\"').append('}');\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\telse\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t_$tracker.put(entry.getValue(),entry.getKey().toString(),false);\n";
                str += bk + "\t\t\t\t\twriteStrategy.getWriter(entry.getValue().getClass()).write(entry.getValue(),cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\tcache.append(',');\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t}\n";
                str += bk + "\t}\n";
                str += bk + "\tif(cache.isCommaLast()){cache.deleteLast();}\n";
                str += bk + "\tcache.append(\"},\");\n";
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
