package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;
import com.jfireframework.codejson.util.NameTool;

public class SetCustomObjectMethodInfo extends AbstractReadMethodInfo
{
    
    public SetCustomObjectMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        str = "if(json.contains(\"" + NameTool.getNameFromMethod(method, strategy) + "\"))\n";
        str += "{\n";
        str += "\t" + entityName + method.getName() + "((" + getParamType().getName() + ")ReaderContext.read(" + getParamType().getName() + ".class," + "json.get(\"" + NameTool.getNameFromMethod(method, strategy) + "\")));\n";
        str += "}\n";
    }
    
}
