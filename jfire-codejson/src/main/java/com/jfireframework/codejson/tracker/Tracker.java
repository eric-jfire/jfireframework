package com.jfireframework.codejson.tracker;

import java.util.HashMap;

public class Tracker
{
    private HashMap<Object, Pathinfo> map = new HashMap<>();
    
    public void put(Object obj, String path)
    {
        Pathinfo info = new Pathinfo(obj, path);
        map.put(obj, info);
    }
    
    public String getPath(Object obj)
    {
        Pathinfo info = map.get(obj);
        return info == null ? null : info.getPath();
    }
    
    public static class Pathinfo
    {
        private Object obj;
        private String path;
        
        public Pathinfo(Object obj, String path)
        {
            this.obj = obj;
            this.path = path;
        }
        
        public Object getObj()
        {
            return obj;
        }
        
        public void setObj(Object obj)
        {
            this.obj = obj;
        }
        
        public String getPath()
        {
            return path;
        }
        
        public void setPath(String path)
        {
            this.path = path;
        }
        
    }
}
