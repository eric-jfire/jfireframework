package com.jfireframework.mvc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class HotwrapClassLoader extends ClassLoader
{
    private ClassLoader                         parent;
    private String[]                            reloadPackages;
    private ConcurrentHashMap<String, Class<?>> classMap = new ConcurrentHashMap<>();
    private File                                parentFile;
                                                
    public HotwrapClassLoader(File parentFile, String[] reloadPackages)
    {
        parent = Thread.currentThread().getContextClassLoader();
        this.reloadPackages = reloadPackages;
        this.parentFile = parentFile;
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
                        File file = new File(parentFile, name.replace(".", "/") + ".class");
                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] src = new byte[inputStream.available()];
                        inputStream.read(src);
                        inputStream.close();
                        Class<?> c = defineClass(name, src, 0, src.length);
                        classMap.put(name, c);
                        return c;
                    }
                    catch (ClassFormatError | IOException e)
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
