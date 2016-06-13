package com.jfireframework.baseutil.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.exception.JustThrowException;

public class SimpleHotswapClassLoader extends ClassLoader
{
    private ClassLoader                                    parent;
    private ConcurrentHashMap<String, Class<?>>            classMap                    = new ConcurrentHashMap<String, Class<?>>();
    private static final ConcurrentHashMap<String, Object> paracLockMap                = new ConcurrentHashMap<String, Object>();
    private final File                                     reloadPathFile;
    private String[]                                       reloadPackages              = new String[0];
    private String[]                                       reloadPackageForClassFiless = new String[0];
    private String[]                                       excludePackages             = new String[0];
    
    public SimpleHotswapClassLoader(String reloadPath)
    {
        parent = Thread.currentThread().getContextClassLoader();
        reloadPathFile = new File(reloadPath);
    }
    
    public void setReloadPackages(String... reloadPackages)
    {
        this.reloadPackages = reloadPackages;
        reloadPackageForClassFiless = new String[reloadPackages.length];
        for (int i = 0; i < reloadPackages.length; i++)
        {
            reloadPackageForClassFiless[i] = reloadPackages[i].replace(".", "/");
        }
    }
    
    public void setExcludePackages(String... excludePackages)
    {
        this.excludePackages = excludePackages;
    }
    
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (classMap.containsKey(name))
        {
            return classMap.get(name);
        }
        synchronized (getClassLoadingLock(name))
        {
            for (String reloadPackage : reloadPackages)
            {
                if (name.startsWith(reloadPackage))
                {
                    boolean canLoad = true;
                    for (String excludePackage : excludePackages)
                    {
                        if (name.startsWith(excludePackage))
                        {
                            canLoad = false;
                            break;
                        }
                    }
                    if (canLoad == false)
                    {
                        continue;
                    }
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
    
    public URL getResource(String name)
    {
        for (String reloadPackageForClass : reloadPackageForClassFiless)
        {
            if (name.startsWith(reloadPackageForClass) && name.endsWith(".class"))
            {
                File file = new File(reloadPathFile, name);
                try
                {
                    return file.toURI().toURL();
                }
                catch (MalformedURLException e)
                {
                    return parent.getResource(name);
                }
            }
            
        }
        return parent.getResource(name);
    }
}
