package com.jfireframework.codejson.methodinfo.impl.write.array;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.WriteStrategy;

/**
 * 用于处理基本类型的数组情况
 * 
 * @author linbin
 * 
 */
public class ReturnArrayBaseMethodInfo extends AbstractWriteArrayMethodInfo
{
    
    public ReturnArrayBaseMethodInfo(Method method, WriteStrategy strategy, String entityName)
    {
        super(method, strategy, entityName);
        
    }
    
    @Override
    protected void writeOneDim(Class<?> rootType, String bk)
    {
        if (strategy != null && strategy.containsStrategyType(rootType))
        {
            str += bk + "baseWriter.write(array1[i1],cache," + entityName + ");\n";
            str += bk + "cache.append(',');\n";
        }
        else
        {
            if (rootType.equals(char.class))
            {
                str += bk + "cache.append('\"').append(array1[i1]).append(\"\\\",\");\n";
            }
            else
            {
                str += bk + "cache.append(array1[i1]).append(',');\n";
            }
        }
    }
    
}
