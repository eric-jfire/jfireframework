package com.jfireframework.codejson.methodinfo.impl.read.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetBaseArrayMethodInfo extends AbstractArrayReadMethodInfo
{
    
    public SetBaseArrayMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
    }
    
    @Override
    protected void readOneDim(String bk)
    {
        str += bk + "array1[i1] = jsonArray1.get" + rootName.substring(0, 1).toUpperCase() + rootName.substring(1) + "(i1);\n";
    }
    
}
