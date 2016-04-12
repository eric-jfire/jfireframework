package com.jfireframework.codejson.methodinfo.impl.write;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.methodinfo.WriteMethodInfo;

public abstract class AbstractWriteMethodInfo implements WriteMethodInfo
{
    protected String        str;
    protected String        getValue;
    protected WriteStrategy strategy;
    protected String        entityName;
    
    public AbstractWriteMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        this.entityName = entityName;
        this.strategy = strategy;
        getValue = entityName + "." + method.getName() + "()";
    }
    
    public String getOutput()
    {
        return str;
    }
}
