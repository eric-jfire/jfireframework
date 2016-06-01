package com.jfireframework.baseutil.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.jfireframework.baseutil.exception.JustThrowException;

public class HotswapClassLoader extends ClassLoader
{
    private final ClassLoader                              parent;
    private String[]                                       reloadPackages    = new String[0];
    // 需要被重载的Class
    private final ConcurrentHashMap<String, Class<?>>      reloadClassMap    = new ConcurrentHashMap<String, Class<?>>();
    // 不需要被重载的，不可变的Class。无论是自定义的还是系统的，都在这个地方
    private final ConcurrentHashMap<String, Class<?>>      immutableClassMap = new ConcurrentHashMap<String, Class<?>>();
    private File[]                                         reloadPaths       = new File[0];
    private static final ConcurrentHashMap<String, Object> paracLockMap      = new ConcurrentHashMap<String, Object>();
    private static final File[]                            classPaths;
    private static final Map<String, classInfo>            classInfos        = new HashMap<String, classInfo>();
    // 排除在外的路径，该路径下的类不会进入自定义的加载流程
    private String[]                                       excludePaths      = new String[0];
    static
    {
        String value = System.getProperty("java.class.path");
        List<File> dirs = new LinkedList<File>();
        for (String each : value.split(";"))
        {
            File file = new File(each);
            if (file.isDirectory())
            {
                dirs.add(file);
            }
            else if (each.endsWith(".jar"))
            {
                JarFile jarFile = null;
                try
                {
                    jarFile = new JarFile(each);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("url地址：'" + each + "'不正确", e);
                }
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements())
                {
                    JarEntry jarEntry = (JarEntry) entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (jarEntry.isDirectory() == false && entryName.endsWith(".class"))
                    {
                        String className = entryName.substring(0, entryName.length() - 6);
                        className = className.replaceAll("/", ".");
                        classInfos.put(className, new classInfo(jarEntry, each));
                    }
                }
                try
                {
                    jarFile.close();
                }
                catch (IOException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
        classPaths = dirs.toArray(new File[dirs.size()]);
    }
    
    static class classInfo
    {
        private final JarEntry jarEntry;
        private final String   jarPath;
        
        public classInfo(JarEntry jarEntry, String jarPath)
        {
            this.jarEntry = jarEntry;
            this.jarPath = jarPath;
        }
        
    }
    
    public void setReloadPaths(String... paths)
    {
        List<File> list = new ArrayList<File>();
        for (String each : paths)
        {
            list.add(new File(each));
        }
        for (File each : classPaths)
        {
            list.add(each);
        }
        reloadPaths = list.toArray(new File[list.size()]);
    }
    
    public void setExcludePaths(String... excludePaths)
    {
        this.excludePaths = excludePaths;
    }
    
    public void setReloadPackages(String... packages)
    {
        reloadPackages = packages;
    }
    
    public HotswapClassLoader()
    {
        reloadPaths = classPaths;
        parent = Thread.currentThread().getContextClassLoader();
    }
    
    public HotswapClassLoader(ClassLoader classLoader)
    {
        reloadPaths = classPaths;
        if (classLoader != null)
        {
            parent = classLoader;
        }
        else
        {
            parent = Thread.currentThread().getContextClassLoader();
        }
    }
    
    public HotswapClassLoader(Map<String, Class<?>> classMap, ClassLoader parent)
    {
        reloadPaths = classPaths;
        this.parent = parent;
        immutableClassMap.putAll(classMap);
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
    
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        Class<?> c = null;
        c = immutableClassMap.get(name);
        if (c != null)
        {
            return c;
        }
        c = reloadClassMap.get(name);
        if (c != null)
        {
            return c;
        }
        synchronized (getClassLoadingLock(name))
        {
            boolean exclude = false;
            for (String each : excludePaths)
            {
                if (name.startsWith(each))
                {
                    exclude = true;
                    break;
                }
            }
            if (exclude == false)
            {
                for (String each : reloadPackages)
                {
                    if (name.startsWith(each))
                    {
                        try
                        {
                            if (classInfos.containsKey(name))
                            {
                                classInfo cInfo = classInfos.get(name);
                                JarFile jarFile = new JarFile(cInfo.jarPath);
                                InputStream inputStream = jarFile.getInputStream(cInfo.jarEntry);
                                byte[] src = new byte[inputStream.available()];
                                inputStream.read(src);
                                inputStream.close();
                                jarFile.close();
                                c = defineClass(name, src, 0, src.length);
                                reloadClassMap.put(name, c);
                                return c;
                            }
                            for (File pathFile : reloadPaths)
                            {
                                File file = new File(pathFile, name.replace(".", "/") + ".class");
                                if (file.exists())
                                {
                                    FileInputStream inputStream = new FileInputStream(file);
                                    byte[] src = new byte[inputStream.available()];
                                    inputStream.read(src);
                                    inputStream.close();
                                    c = defineClass(name, src, 0, src.length);
                                    reloadClassMap.put(name, c);
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
            }
            if (c == null)
            {
                c = parent.loadClass(name);
                if (c == null)
                {
                    throw new ClassNotFoundException(name);
                }
                resolveClass(c);
            }
            immutableClassMap.put(name, c);
            return c;
        }
    }
}
