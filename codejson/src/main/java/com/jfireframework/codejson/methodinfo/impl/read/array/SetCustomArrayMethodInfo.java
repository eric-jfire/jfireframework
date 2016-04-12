package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.util.NameTool;

public class SetCustomArrayMethodInfo extends AbstractArrayReadMethodInfo
{
    
    public SetCustomArrayMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
    }
    
    @Override
    protected void readOneDim(String bk)
    {
        str += bk + "array1[i1] = ReaderContext.read(" + NameTool.getRootType(getParamType()).getName() + ".class,jsonArray1.get(i1));\n";
    }
    
}
