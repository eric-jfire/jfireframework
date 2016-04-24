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
                str += bk + "\tcache.append('[');\n";
                str += bk + "\tObject valueTmp = null;\n";
                str += bk + "\tIterator it = ((Iterable)array1[i1]).iterator();\n";
                str += bk + "\tint count = 0;\n";
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
                str += bk + "\t\t\t\t_$tracker.reset(_$reIndexarray0);\n";
                str += bk + "\t\t\t\tint _$indexarray0 = _$tracker.indexOf(valueTmp);\n";
                str += bk + "\t\t\t\tif(_$indexarray0 != -1)\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\tJsonWriter writerarray0_1 = writeStrategy.getTrackerType(valueTmp.getClass());\n";
                str += bk + "\t\t\t\t\tif(writerarray0_1 != null)\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\twriterarray0_1.write(valueTmp,cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t\telse\n";
                str += bk + "\t\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$indexarray0)).append('\"').append('}');\n";
                str += bk + "\t\t\t\t\t}\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t\telse\n";
                str += bk + "\t\t\t\t{\n";
                str += bk + "\t\t\t\t\t_$tracker.put(valueTmp,\"[\"+count+']',true);\n";
                str += bk + "\t\t\t\t\twriteStrategy.getWriter(valueTmp.getClass()).write(valueTmp,cache," + entityName + ",_$tracker);\n";
                str += bk + "\t\t\t\t}\n";
                str += bk + "\t\t\t}\n";
                str += bk + "\t\t\tcache.append(',');\n";
                str += bk + "\t\t\tcount+=1;\n";
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
