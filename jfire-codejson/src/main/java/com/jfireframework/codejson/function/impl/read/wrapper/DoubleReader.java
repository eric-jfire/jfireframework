package com.jfireframework.codejson.function.impl.read.wrapper;

import java.lang.reflect.Type;
import com.jfireframework.codejson.function.JsonReader;

public class DoubleReader implements JsonReader
{
    
    @Override
    public Object read(Type entityClass, Object value)
    {
        return Double.valueOf((String) value);
    }
    
}
