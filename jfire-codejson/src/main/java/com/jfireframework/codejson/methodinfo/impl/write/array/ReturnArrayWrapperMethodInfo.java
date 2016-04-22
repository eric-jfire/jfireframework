package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.wrapper.WrapperWriter;

public class ReturnArrayWrapperMethodInfo extends AbstractWriteArrayMethodInfo
{
    public ReturnArrayWrapperMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        
    }
    
    @Override
    protected void writeOneDim(Class<?> rootType, String bk)
    {
        if (strategy != null && (strategy.containsStrategyType(rootType) && strategy.getWriter(rootType) instanceof WrapperWriter == false))
        {
            if (strategy.isUseTracker())
            {
                str += bk + "baseWriter.write(array1[i1],cache," + entityName + ",_$tracker);\n";
            }
            else
            {
                str += bk + "baseWriter.write(array1[i1],cache," + entityName + ",null);\n";
            }
            str += bk + "cache.append(',');\n";
        }
        else
        {
            if (rootType.equals(Character.class) || rootType.equals(String.class))
            {
                str += bk + "cache.append('\"').append(array1[i1]).append('\"').append(',');\n";
            }
            else
            {
                str += bk + "cache.append(array1[i1]).append(',');\n";
            }
        }
    }
    
}
