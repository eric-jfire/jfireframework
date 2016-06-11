package com.jfireframework.baseutil.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.exception.JustThrowException;

public class SimpleHotswapClassLoader extends ClassLoader
{
    private ClassLoader                                    parent;
    private ConcurrentHashMap<String, Class<?>>            classMap     = new ConcurrentHashMap<String, Class<?>>();
    private static final ConcurrentHashMap<String, Object> paracLockMap = new ConcurrentHashMap<String, Object>();
    private final String                                   reloadPackage;
    private final File                                     reloadPathFile;
    
    public SimpleHotswapClassLoader(String reloadPath, String reloadPackage)
    {
        parent = Thread.currentThread().getContextClassLoader();
        this.reloadPackage = reloadPackage;
        reloadPathFile = new File(reloadPath);
    }
    
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (classMap.containsKey(name))
        {
            return classMap.get(name);
        }
        synchronized (getClassLoadingLock(name))
        {
            if (name.startsWith(reloadPackage))
            {
                try
                {
                    File file = new File(reloadPathFile, name.replace(".", "/") + ".class");
                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] src = new byte[inputStream.available()];
                    inputStream.read(src);
                    inputStream.close();
                    Class<?> c = defineClass(name, src, 0, src.length);
                    classMap.put(name, c);
                    return c;
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
            }
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null)
            {
                try
                {
                    c = parent.loadClass(name);
                }
                catch (ClassNotFoundException e)
                {
                }
                if (c == null)
                {
                    c = findClass(name);
                }
            }
            classMap.put(name, c);
            return c;
        }
    }
    
    protected Object getClassLoadingLock(String name)
    {
        Object result = paracLockMap.get(name);
        if (result == null)
        {
            Object tmp = new Object();
            result = paracLockMap.putIfAbsent(name, tmp);
            if (result == null)
            {
                result = tmp;
            }
        }
        return result;
    }
}
