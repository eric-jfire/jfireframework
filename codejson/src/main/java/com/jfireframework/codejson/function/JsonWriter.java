package com.jfireframework.codejson.function;

import com.jfireframework.baseutil.collection.StringCache;

public interface JsonWriter
{
    /**
     * 将target对象以json格式输出到cache中
     * 
     * @param field
     * @param entity TODO
     * @return
     */
    public void write(Object field, StringCache cache, Object entity);
    
    public void write(int field, StringCache cache, Object entity);
    
    public void write(float field, StringCache cache, Object entity);
    
    public void write(double field, StringCache cache, Object entity);
    
    public void write(long field, StringCache cache, Object entity);
    
    public void write(byte field, StringCache cache, Object entity);
    
    public void write(char field, StringCache cache, Object entity);
    
    public void write(short field, StringCache cache, Object entity);
    
    public void write(boolean field, StringCache cache, Object entity);
}
