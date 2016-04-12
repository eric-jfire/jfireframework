package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.util.NameTool;

public class SetWarpperArrayMethodInfo extends AbstractArrayReadMethodInfo
{
    
    public SetWarpperArrayMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
    }
    
    @Override
    protected void readOneDim(String bk)
    {
        String name = NameTool.getRootType(getParamType()).getSimpleName();
        str += bk + "array1[i1] = jsonArray1.getW" + name.substring(0, 1).toUpperCase() + name.substring(1) + "(i1);\n";
    }
    
}
