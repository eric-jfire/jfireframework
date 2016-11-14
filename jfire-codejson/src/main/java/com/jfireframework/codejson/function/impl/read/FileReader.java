package com.jfireframework.codejson.function.impl.read;

import java.io.File;
import java.lang.reflect.Type;
import com.jfireframework.codejson.function.JsonReader;

public class FileReader implements JsonReader
{
    
    @Override
    public Object read(Type entityType, Object value)
    {
        return new File((String) value);
    }
    
}
