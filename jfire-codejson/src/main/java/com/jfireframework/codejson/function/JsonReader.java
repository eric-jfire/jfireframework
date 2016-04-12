package com.jfireframework.codejson.function;

import java.lang.reflect.Type;

public interface JsonReader
{
    public Object read(Type entityType, Object value);
}
