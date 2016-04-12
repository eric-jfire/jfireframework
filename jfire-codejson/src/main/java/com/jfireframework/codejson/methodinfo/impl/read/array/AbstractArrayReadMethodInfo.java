package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.methodinfo.impl.read.AbstractReadMethodInfo;
import com.jfireframework.codejson.util.NameTool;

public abstract class AbstractArrayReadMethodInfo extends AbstractReadMethodInfo
{
    protected String rootName;
    
    public AbstractArrayReadMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        Class<?> rootType = NameTool.getRootType(method.getParameterTypes()[0]);
        rootName = rootType.getName();
        int dim = NameTool.getDimension(method.getParameterTypes()[0]);
        str = "if(json.contains(\"" + NameTool.getNameFromMethod(method, strategy) + "\"))\n";
        str += "{\n";
        str += "\t" + "JsonArray jsonArray" + dim + " = json.getJsonArray(\"" + NameTool.getNameFromMethod(method, strategy) + "\");\n";
        String bk = "\t";
        for (int i = dim; i > 0; i--)
        {
            str += bk + "int l" + i + " =jsonArray" + i + ".size();\n";
            str += bk + NameTool.buildDimTypeName(rootName, i) + " array" + i + " = " + NameTool.buildNewDimTypeName(rootName, i, "l" + i) + ";\n";
            String iName = "i" + i;
            str += bk + "for(int " + iName + " = 0;" + iName + " <l" + i + ";" + iName + "++)\n";
            str += bk + "{\n";
            bk += "\t";
            if (i > 1)
            {
                str += bk + "JsonArray jsonArray" + (i - 1) + " = jsonArray" + i + ".getJsonArray(i" + i + ");\n";
            }
        }
        readOneDim(bk);
        bk = bk.substring(0, bk.length() - 1);
        str += bk + "}\n";
        for (int i = 2; i <= dim; i++)
        {
            str += bk + "array" + i + "[i" + i + "] = array" + (i - 1) + " ;\n";
            bk = bk.substring(0, bk.length() - 1);
            str += bk + "}\n";
        }
        str += "\t" + entityName + method.getName() + "(array" + dim + ");\n";
        str += "}\n";
    }
    
    protected abstract void readOneDim(String bk);
}
