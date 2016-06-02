package com.jfireframework.baseutil.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.jfireframework.baseutil.exception.JustThrowException;

public class HotswapClassLoader extends ClassLoader
{
    private final ClassLoader                              parent;
    private String[]                                       reloadPackages    = new String[0];
    private File[]                                         reloadPaths       = new File[0];
    // 需要被重载的Class
    private final ConcurrentHashMap<String, Class<?>>      reloadClassMap    = new ConcurrentHashMap<String, Class<?>>();
    // 不需要被重载的，不可变的Class。无论是自定义的还是系统的，都在这个地方
    private final ConcurrentHashMap<String, Class<?>>      immutableClassMap = new ConcurrentHashMap<String, Class<?>>();
    private static final ConcurrentHashMap<String, Object> paracLockMap      = new ConcurrentHashMap<String, Object>();
    private static final Map<String, classInfo>            classInfos        = new HashMap<String, classInfo>();
    // 排除在外的路径，该路径下的类不会进入自定义的加载流程
    private final Set<String>                              excludeClasses    = new HashSet<String>();
    
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
    
    public static void addLibPath(String libPath)
    {
        File[] files = new File(libPath).listFiles();
        for (File file : files)
        {
            if (file.isDirectory() == false && file.getName().endsWith(".jar"))
            {
                JarFile jarFile = null;
                try
                {
                    jarFile = new JarFile(file);
                }
                catch (IOException e)
                {
                    throw new JustThrowException("url地址：'" + file.getAbsolutePath() + "'不正确", e);
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
                        classInfos.put(className, new classInfo(jarEntry, file.getAbsolutePath()));
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
    }
    
    public void setReloadPaths(String... paths)
    {
        List<File> files = new LinkedList<File>();
        for (String each : paths)
        {
            files.add(new File(each));
        }
        reloadPaths = files.toArray(new File[files.size()]);
    }
    
    public void setExcludeClasses(String... excludeClasses)
    {
        for (String each : excludeClasses)
        {
            this.excludeClasses.add(each);
        }
    }
    
    public void setReloadPackages(String... packages)
    {
        reloadPackages = packages;
    }
    
    public HotswapClassLoader()
    {
        parent = Thread.currentThread().getContextClassLoader();
    }
    
    public HotswapClassLoader(HotswapClassLoader classLoader)
    {
        if (classLoader != null)
        {
            parent = classLoader;
        }
        else
        {
            parent = Thread.currentThread().getContextClassLoader();
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
            if (excludeClasses.contains(name))
            {
                exclude = true;
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
