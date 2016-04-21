package com.jfireframework.codejson.tracker;

public class Tracker
{
    private Pathinfo[] array = new Pathinfo[10];
    private int        count = 0;
    
    public void reset(Object obj)
    {
        for (int i = 0; i < count; i++)
        {
            if (array[i].getObj() == obj)
            {
                count = i + 1;
                break;
            }
        }
    }
    
    public void put(Object obj, String path)
    {
        if (count < array.length)
        {
            array[count++] = new Pathinfo(obj, path);
        }
        else
        {
            Pathinfo[] tmp = new Pathinfo[array.length + 10];
            System.arraycopy(array, 0, tmp, 0, count);
            tmp[count++] = new Pathinfo(obj, path);
        }
    }
    
    public String getPath(Object obj)
    {
        for (int i = 0; i < count; i++)
        {
            if (array[i].getObj() == obj)
            {
                return array[i].getPath();
            }
        }
        return null;
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
