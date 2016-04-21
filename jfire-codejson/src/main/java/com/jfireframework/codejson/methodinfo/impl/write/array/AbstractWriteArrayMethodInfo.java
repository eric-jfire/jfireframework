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
                str += "\tif(((Tracker)$4).getPath(" + fieldName + ")==null)\n";
                str += "\t{\n";
                str += "\t\t((Tracker)$4).reset(" + entityName + ");\n";
                str += "\t\tString newPath = ((Tracker)$4).getPath(" + entityName + ")+'.'+\"" + fieldName + "\";\n";
                str += "\t\t((Tracker)$4).put(" + fieldName + ",newPath);\n";
                str += "\t}\n";
                str += "\twriteStrategy.getWriterByField(\"" + key + "\").write(" + arrayName + ",cache," + entityName + ",(Tracker)$4);\n";
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
                str += "\t((Tracker)$4).reset(" + entityName + ");\n";
                str += "\tString path = ((Tracker)$4).getPath(" + arrayName + ");\n";
                str += "\tif(path!=null)\n";
                str += "\t{\n";
                str += "\t\tif(writeStrategy.containsTrackerType(" + arrayName + ".getClass()))\n";
                str += "\t\t{\n";
                str += "\t\t\tJsonWriter writer = writeStrategy.getTrackerType(" + arrayName + ".getClass());\n";
                str += "\t\t\twriter.write(" + arrayName + ",cache," + entityName + ",(Tracker)$4);\n";
                str += "\t\t}\n";
                str += "\t\telse\n";
                str += "\t\t{\n";
                str += "\t\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(path).append('\"').append('}');\n";
                str += "\t\t}\n";
                str += "\t\tcache.append(',');\n";
                str += "\t}\n";
                str += "\telse\n";
                str += "\t{\n";
                str += "\t\tString newPath = ((Tracker)$4).getPath(" + entityName + ")+'.'+\"" + fieldName + "\";\n";
                str += "\t\t((Tracker)$4).put(" + arrayName + ",newPath);\n";
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
                String now = "array" + (dim - i);
                String next = "array" + (dim - i - 1);
                if (i != 0)
                {
                    str += bk + "if(" + now + " != null)\n" + bk + "{\n";
                    if (strategy != null && strategy.isUseTracker())
                    {
                        String pathname = "path" + now;
                        str += nextBk + "((Tracker)$4).reset(" + pre + ");\n";
                        str += nextBk + "String " + pathname + " = ((Tracker)$4).getPath(" + now + ");\n";
                        str += nextBk + "if(" + pathname + "!=null)\n";
                        str += nextBk + "{\n";
                        str += nextBk + "\tif(writeStrategy.containsTrackerType(" + now + ".getClass()))\n";
                        str += nextBk + "\t{\n";
                        str += nextBk + "\t\twriteStrategy.getTrackerType(" + now + ".getClass()).write(" + now + ",cache," + entityName + ",(Tracker)$4);\n";
                        str += nextBk + "\t}\n";
                        str += nextBk + "\telse\n";
                        str += nextBk + "\t{\n";
                        str += nextBk + "\t\tcache.append(\"{\\\"$ref\\\":\\\"\").append(" + pathname + ").append('\"').append('}');\n";
                        str += nextBk + "\t}\n";
                        str += nextBk + "}\n";
                        str += nextBk + "else\n";
                        str += nextBk + "{\n";
                        str += nextBk + "\t" + pathname + " = ((Tracker)$4).getPath(" + pre + ");\n";
                        str += nextBk + "\t" + pathname + " += \"[\"+i" + (dim - i + 1) + "+']';\n";
                        str += nextBk + "\t((Tracker)$4).put(" + now + "," + pathname + ");\n";
                        nextBk += "\t";
                        // str += nextBk + "}\n";
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
                nextBk = bk + "\t";
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
