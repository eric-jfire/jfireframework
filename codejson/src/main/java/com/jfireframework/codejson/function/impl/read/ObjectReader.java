package com.jfireframework.codejson.function.impl.read;

import java.lang.reflect.Type;
import com.jfireframework.codejson.function.JsonReader;

public class ObjectReader implements JsonReader
{
    
    @Override
    public Object read(Type entityType, Object value)
    {
        return value;
    }
    
}
