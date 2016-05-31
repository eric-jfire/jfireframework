package com.jfireframework.baseutil.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HotwrapClassLoader extends ClassLoader
{
    private ClassLoader                         parent;
    private String[]                            reloadPackages;
    private ConcurrentHashMap<String, Class<?>> classMap = new ConcurrentHashMap<String, Class<?>>();
    private File[]                              pathFils;
    
    public void setClassPaths(String... paths)
    {
        List<File> list = new ArrayList<File>();
        for (String each : paths)
        {
            list.add(new File(each));
        }
        pathFils = list.toArray(new File[list.size()]);
    }
    
    public void setReloadPackages(String... packages)
    {
        reloadPackages = packages;
    }
    
    public HotwrapClassLoader()
    {
        parent = Thread.currentThread().getContextClassLoader();
    }
    
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (classMap.containsKey(name))
        {
            return classMap.get(name);
        }
        synchronized (getClassLoadingLock(name))
        {
            for (String each : reloadPackages)
            {
                if (name.startsWith(each))
                {
                    try
                    {
                        for (File pathFile : pathFils)
                        {
                            File file = new File(pathFile, name.replace(".", "/") + ".class");
                            if (file.exists())
                            {
                                FileInputStream inputStream = new FileInputStream(file);
                                byte[] src = new byte[inputStream.available()];
                                inputStream.read(src);
                                inputStream.close();
                                Class<?> c = defineClass(name, src, 0, src.length);
                                classMap.put(name, c);
                                return c;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
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
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
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
    
}
