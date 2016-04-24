package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.methodinfo.MethodInfoBuilder;
import com.jfireframework.codejson.methodinfo.impl.write.AbstractWriteMethodInfo;
import com.jfireframework.codejson.util.NameTool;

public abstract class AbstractWriteArrayMethodInfo extends AbstractWriteMethodInfo
{
    protected Method method;
    
    public AbstractWriteArrayMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        this.method = method;
        Class<?> returnType = method.getReturnType();
        int dim = NameTool.getDimension(returnType);
        Class<?> rootType = NameTool.getRootType(returnType);
        String rootName = rootType.getName();
        str = "if(" + getValue + " != null)\n{\n";
        String key = method.getDeclaringClass().getName() + '.' + NameTool.getNameFromMethod(method, strategy);
        String arrayName = "array" + dim;
        String fieldName = NameTool.getNameFromMethod(method, strategy);
        str += "\t" + NameTool.buildDimTypeName(rootName, dim) + ' ' + arrayName + " = " + getValue + ";\n";
        str += "\tcache.append(\"\\\"" + NameTool.getNameFromMethod(method, strategy) + "\\\":\");\n";
        if (strategy != null && strategy.containsStrategyField(key))
        {
            if (strategy.isUseTracker())
            {
                str += "\t_$tracker.reset(_$reIndex);\n";
                str += "\tint _$index = _$tracker.indexOf(" + arrayName + ");\n";
                str += "\tif(_$index == -1)\n";
                str += "\t{\n";
                str += "\t\t_$tracker.put(" + arrayName + ",\"" + fieldName + "\",false);\n";
                str += "\t}\n";
                str += "\twriteStrategy.getWriterByField(\"" + key + "\").write(" + arrayName + ",cache," + entityName + ",_$tracker);\n";
            }
            else
            {
                str += "\twriteStrategy.getWriterByField(\"" + key + "\").write(" + arrayName + ",cache," + entityName + ",null);\n";
                str += "\tcache.append(',');\n";
                str += "}\n";
            }
        }
        else
        {
            String bk = "\t";
            String nextBk = "\t\t";
            if (strategy != null && strategy.isUseTracker())
            {
                str += "\t_$tracker.reset(_$reIndex);\n";
                str += "\tint _$index = _$tracker.indexOf(" + arrayName + ");\n";
                str += "\tif(_$index != -1)\n";
                str += "\t{\n";
                str += "\t\tJsonWriter writer = writeStrategy.getTrackerType(" + arrayName + ".getClass());\n";
                str += "\t\tif(writer != null)\n";
                str += "\t\t{\n";
                str += "\t\t\twriter.write(" + arrayName + ",cache," + entityName + ",_$tracker);\n";
                str += "\t\t}\n";
                str += "\t\telse\n";
                str += "\t\t{\n";
                str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(_$index)).append('\"').append('}');\n";
                str += "\t\t}\n";
                str += "\t\tcache.append(',');\n";
                str += "\t}\n";
                str += "\telse\n";
                str += "\t{\n";
                str += "\t\tint _$reIndexarray" + dim + " = _$tracker.put(" + arrayName + ",\"" + fieldName + "\",false);\n";
                bk = "\t\t";
                nextBk = "\t\t\t";
            }
            if (strategy != null && (MethodInfoBuilder.wrapperSet.contains(rootType) || (rootType.isPrimitive() && strategy.containsStrategyType(rootType))))
            {
                str += bk + "JsonWriter baseWriter = writeStrategy.getWriter(" + rootType.getName() + ".class);\n";
            }
            for (int i = 0; i < dim; i++)
            {
                String pre = "array" + (dim - i + 1);
                String preIndex = "_$reIndex" + pre;
                String now = "array" + (dim - i);
                String nowIndex = "_$reIndex" + now;
                String next = "array" + (dim - i - 1);
                String writerName = "writer" + now;
                if (i != 0)
                {
                    str += bk + "if(" + now + " != null)\n" + bk + "{\n";
                    if (strategy != null && strategy.isUseTracker())
                    {
                        str += nextBk + "_$tracker.reset(" + preIndex + ");\n";
                        str += nextBk + "int " + nowIndex + " = _$tracker.indexOf(" + now + ");\n";
                        str += nextBk + "if(" + nowIndex + "!= -1)\n";
                        str += nextBk + "{\n";
                        str += nextBk + "\tJsonWriter " + writerName + " = writeStrategy.getTrackerType(" + now + ".getClass());\n";
                        str += nextBk + "\tif(" + writerName + " != null)\n";
                        str += nextBk + "\t{\n";
                        str += nextBk + "\t\t" + writerName + ".write(" + now + ",cache," + entityName + ",_$tracker);\n";
                        str += nextBk + "\t}\n";
                        str += nextBk + "\telse\n";
                        str += nextBk + "\t{\n";
                        str += nextBk + "\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(_$tracker.getPath(" + nowIndex + ")).append('\"').append('}');\n";
                        str += nextBk + "\t}\n";
                        str += nextBk + "}\n";
                        str += nextBk + "else\n";
                        str += nextBk + "{\n";
                        str += nextBk + "\t_$tracker.put(" + now + ",\"[\"+i" + (dim - i + 1) + "+']',true);\n";
                        nextBk += "\t";
                    }
                }
                else
                {
                    nextBk = bk;
                }
                str += nextBk + "cache.append('[');\n";
                String index = "i" + (dim - i);
                String lengthStr = "h" + (dim - i);
                str += nextBk + "int " + lengthStr + " = " + now + ".length;\n";
                str += nextBk + "for(int " + index + "=0;" + index + "<" + lengthStr + ";" + index + "++)\n" + nextBk + "{\n";
                if (i != dim - 1)
                {
                    str += nextBk + '\t' + NameTool.buildDimTypeName(rootName, dim - i - 1) + " " + next + " = " + now + "[" + index + "];\n";
                }
                bk = nextBk + '\t';
                nextBk = bk + '\t';
            }
            writeOneDim(rootType, bk);
            bk = bk.substring(0, bk.length() - 1);
            if (strategy != null && strategy.isUseTracker())
            {
                for (int i = dim; i > 0; i--)
                {
                    if (i == dim)
                    {
                        nextBk = bk.substring(0, bk.length() - 1);
                        str += bk + "}\n";
                        str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
                        str += bk + "cache.append(\"],\");\n";
                        str += nextBk + "}\n";
                        bk = nextBk.substring(0, nextBk.length() - 1);
                    }
                    else
                    {
                        str += bk + "}\n";
                        nextBk = bk.substring(0, bk.length() - 1);
                        str += nextBk + "}\n";
                        str += nextBk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
                        str += nextBk + "cache.append(\"],\");\n";
                        nextBk = bk.substring(0, nextBk.length() - 1);
                        str += nextBk + "}\n";
                        bk = nextBk.substring(0, nextBk.length() - 1);
                    }
                }
                if (strategy != null && strategy.isUseTracker())
                {
                    str += "}\n";
                }
                
            }
            else
            {
                for (int i = dim; i > 0; i--)
                {
                    nextBk = bk.substring(0, bk.length() - 1);
                    str += bk + "}\n";
                    str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
                    str += bk + "cache.append(\"],\");\n";
                    str += nextBk + "}\n";
                    if (i != 1)
                    {
                        bk = nextBk.substring(0, nextBk.length() - 1);
                    }
                }
            }
        }
    }
    
    protected abstract void writeOneDim(Class<?> rootType, String bk);
    
}
