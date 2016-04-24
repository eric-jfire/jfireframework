package com.jfireframework.codejson.tracker;

import com.jfireframework.baseutil.collection.StringCache;

public class Tracker
{
    private Object[]    objs    = new Object[10];
    private String[]    paths   = new String[10];
    private boolean[]   isArray = new boolean[10];
    private int         count   = 0;
    private StringCache cache   = new StringCache(128);
    
    public void clear()
    {
        for (int i = 0; i < objs.length; i++)
        {
            objs[i] = null;
            paths[i] = null;
        }
        count = 0;
    }
    
    public void reset(int reIndex)
    {
        count = reIndex + 1;
    }
    
    /**
     * 放入一个对象，还有对象的引用名称，以及对象是否是数组
     * 
     * @param obj
     * @param path
     * @param isArray
     * @return
     */
    public int put(Object obj, String path, boolean isArray)
    {
        if (count < objs.length)
        {
            ;
        }
        else
        {
            Object[] tmp1 = new Object[objs.length + 10];
            String[] tmp2 = new String[paths.length + 10];
            boolean[] tmp3 = new boolean[this.isArray.length + 10];
            System.arraycopy(objs, 0, tmp1, 0, count);
            System.arraycopy(paths, 0, tmp2, 0, count);
            System.arraycopy(this.isArray, 0, tmp3, 0, count);
            objs = tmp1;
            paths = tmp2;
            this.isArray = tmp3;
        }
        objs[count] = obj;
        paths[count] = path;
        this.isArray[count] = isArray;
        count += 1;
        return count;
    }
    
    public int indexOf(Object obj)
    {
        for (int i = 0; i < count; i++)
        {
            if (objs[i] == obj)
            {
                return i;
            }
        }
        return -1;
    }
    
    public String getPath(int index)
    {
        cache.clear();
        cache.append(paths[0]);
        for (int i = 1; i < index; i++)
        {
            if (isArray[i])
            {
                cache.append(paths[i]);
            }
            else
            {
                cache.append('.').append(paths[i]);
            }
        }
        return cache.toString();
    }
    
}
