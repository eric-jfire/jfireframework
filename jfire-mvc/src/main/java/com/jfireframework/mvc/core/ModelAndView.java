package com.jfireframework.mvc.core;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.collection.ByteCache;

public class ModelAndView
{
    private Map<String, Object> data   = null;
    private String              modelName;
    private volatile boolean    direct = false;
    private volatile byte[]     directBytes;
    private ByteCache           cache;
    private volatile boolean    cached = false;
    // 视图的类型
    private String              contentType;
    private Map<String, String> header;
    
    public void setHeader(String key, String value)
    {
        if (header == null)
        {
            header = new HashMap<String, String>();
        }
        header.put(key, value);
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public ModelAndView(String modelName)
    {
        this.modelName = modelName;
    }
    
    public ModelAndView(String modelName, boolean direct)
    {
        this.modelName = modelName;
        this.direct = direct;
        cache = new ByteCache(512);
    }
    
    public void addObject(String key, Object value)
    {
        if (data == null)
        {
            data = new HashMap<String, Object>();
        }
        data.put(key, value);
    }
    
    public void setDataMap(Map<String, Object> data)
    {
        this.data = data;
    }
    
    public String getModelName()
    {
        return modelName;
    }
    
    public Map<String, Object> getData()
    {
        return data;
    }
    
    public boolean isDirect()
    {
        return direct;
    }
    
    public byte[] getDirectBytes()
    {
        return directBytes;
    }
    
    public void setDirectBytes(byte[] directBytes)
    {
        this.directBytes = directBytes;
        cached = true;
    }
    
    public ByteCache getCache()
    {
        return cache;
    }
    
    public boolean cached()
    {
        return cached;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public Map<String, String> getHeader()
    {
        return header;
    }
    
    public void setHeader(Map<String, String> header)
    {
        this.header = header;
    }
    
}
