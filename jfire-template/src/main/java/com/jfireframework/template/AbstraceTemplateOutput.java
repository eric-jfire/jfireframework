package com.jfireframework.template;

import com.jfireframework.baseutil.collection.StringCache;

public abstract class AbstraceTemplateOutput implements TemplateOutput
{
    protected ThreadLocal<StringCache> cacheLocal = new ThreadLocal<StringCache>() {
        protected StringCache initialValue()
        {
            return new StringCache();
        }
    };
    
    @Override
    public void print(int i)
    {
        cacheLocal.get().append(i);
    }
    
    @Override
    public void print(boolean b)
    {
        cacheLocal.get().append(b);
    }
    
    @Override
    public void print(char c)
    {
        cacheLocal.get().append(c);
    }
    
    @Override
    public void print(float f)
    {
        cacheLocal.get().append(f);
    }
    
    @Override
    public void print(double d)
    {
        cacheLocal.get().append(d);
    }
    
    @Override
    public void print(short s)
    {
        cacheLocal.get().append(s);
    }
    
    @Override
    public void print(long l)
    {
        cacheLocal.get().append(l);
    }
    
    @Override
    public void print(byte b)
    {
        cacheLocal.get().append(b);
    }
    
    @Override
    public void print(Object object)
    {
        cacheLocal.get().append(object);
    }
    
}
