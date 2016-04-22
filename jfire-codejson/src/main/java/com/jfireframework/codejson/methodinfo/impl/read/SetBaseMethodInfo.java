package com.jfireframework.codejson.methodinfo.impl.read;

import java.lang.reflect.Method;
import com.jfireframework.codejson.function.ReadStrategy;

public class SetBaseMethodInfo extends AbstractReadMethodInfo
{
    
    public SetBaseMethodInfo(Method method, ReadStrategy strategy)
    {
        super(method, strategy);
        String jsonGetMethodName = "get" + getParamType().getName().substring(0, 1).toUpperCase() + getParamType().getName().substring(1);
        str = "if(json.contains(\"" + fieldName + "\"))\n";
        str += "{\n";
        if (strategy != null && (strategy.containsStrategyField(strategyFieldName) || strategy.containsStrategyType(getParamType())))
        {
            if (getParamType().equals(char.class))
            {
                if (strategy.containsStrategyField(strategyFieldName))
                {
                    str += "\tCharacter c = " + "(Character)readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
                else
                {
                    str += "\tCharacter c = " + "(Character)readStrategy.getReader(" + getParamType() + ".class).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
            }
            else if (getParamType().equals(boolean.class))
            {
                if (strategy.containsStrategyField(strategyFieldName))
                {
                    str += "\tBoolean b = " + "(Boolean)readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
                else
                {
                    str += "\tBoolean b = " + "(Boolean)readStrategy.getReader($sig[0]).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
            }
            else
            {
                if (strategy.containsStrategyField(strategyFieldName))
                {
                    str += "\tNumber num = " + "(Number)readStrategy.getReaderByField(\"" + strategyFieldName + "\").read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
                else
                {
                    str += "\tNumber num = " + "(Number)readStrategy.getReader(" + getParamType().getName() + ".class).read(" + getParamType().getName() + ".class,json.get(\"" + fieldName + "\"));\n";
                }
            }
            if (getParamType().equals(int.class))
            {
                str += "\t" + entityName + method.getName() + "(num.intValue());\n";
            }
            else if (getParamType().equals(short.class))
            {
                str += "\t" + entityName + method.getName() + "(num.shortValue());\n";
            }
            else if (getParamType().equals(long.class))
            {
                str += "\t" + entityName + method.getName() + "(num.LongValue());\n";
            }
            else if (getParamType().equals(float.class))
            {
                str += "\t" + entityName + method.getName() + "(num.floatValue());\n";
            }
            else if (getParamType().equals(double.class))
            {
                str += "\t" + entityName + method.getName() + "(num.doubleValue());\n";
            }
            else if (getParamType().equals(byte.class))
            {
                str += "\t" + entityName + method.getName() + "(num.byteValue());\n";
            }
            else if (getParamType().equals(boolean.class))
            {
                str += "\t" + entityName + method.getName() + "(b.booleanValue());\n";
            }
            else if (getParamType().equals(Character.class))
            {
                str += "\t" + entityName + method.getName() + "(c.charValue());\n";
                
            }
        }
        else
        {
            str += "\t" + entityName + method.getName() + "(json." + jsonGetMethodName + "(\"" + fieldName + "\"));\n";
        }
        str += "}\n";
    }
    
}
