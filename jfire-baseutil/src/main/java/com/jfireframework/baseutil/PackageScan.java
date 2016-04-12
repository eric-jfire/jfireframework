package com.jfireframework.baseutil;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.jfireframework.baseutil.collection.set.LightSet;

/**
 * 包扫描类，可以扫描某个路径下所有的class
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class PackageScan
{
    /**
     * 根据给定的包名，返回包下面所有的类的全限定名
     * 支持过滤语法，过滤语法以：开始。
     * 规则有：
     * （1）以“in~”开头，代表必须包含后面的包名.返回的字符串数组中的字符串均包含后面的包名
     * （2）以“out~”开头，代表不包含后面的包名。返回的字符串数组中的字符串均不包含后面的包名
     * 
     * @param packageName
     * @return
     */
    public static String[] scan(String packageName)
    {
        String filterNames = null;
        if (packageName.contains(":"))
        {
            filterNames = packageName.split(":")[1];
            packageName = packageName.split(":")[0];
        }
        LightSet<String> classNames = new LightSet<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String resourceName = packageName.replaceAll("\\.", "/");
        URL url = loader.getResource(resourceName);
        if (url != null)
        {
            if (url.getProtocol().contains("file"))
            {
                try
                {
                    File urlFile = new File(url.toURI());
                    packageName = packageName.substring(0, packageName.lastIndexOf(".") + 1);
                    findClassNamesByFile(packageName, urlFile, classNames);
                }
                catch (URISyntaxException e)
                {
                    throw new RuntimeException("路径：'" + url.toString() + "'不正确", e);
                }
            }
            else if (url.getProtocol().contains("jar"))
            {
                getClassNamesByJar(url, resourceName, classNames);
            }
        }
        else
        {
            getClassNameByJars(((URLClassLoader) loader).getURLs(), packageName, classNames);
        }
        doFilter(filterNames, classNames);
        return classNames.toArray(String.class);
    }
    
    /**
     * 将url所表示的jar路径的jar读取，并且将其中的class文件放入到list中，返回list
     * 
     * @param url
     * @param packageName
     * @return
     * @throws IOException
     */
    private static void getClassNamesByJar(URL url, String packageName, LightSet<String> classNames)
    {
        JarFile jarFile = null;
        try
        {
            // 获取正确并且完成的jar路径的url表示
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException("url地址：'" + url.toString() + "'不正确", e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            String entryName = jarEntry.getName();
            // 将符合条件的class文件的全限定名加入到list中
            if (entryName.endsWith("class") && !entryName.contains("$") && entryName.startsWith(packageName))
            {
                String className = entryName.substring(0, entryName.indexOf(".class"));
                className = className.replaceAll("/", ".");
                classNames.addValue(className);
            }
        }
    }
    
    /**
     * 如果packageFile是class文件，则将该类的全限定名加入到list中。如果是文件夹就遍历，然后对每一个子文件或者文件夹重复该过程
     * 
     * @param packageName 当前的前缀包名
     * @param packageFile 当前的文件
     * @param list
     */
    private static void findClassNamesByFile(String packageName, File packageFile, LightSet<String> classNames)
    {
        if (packageFile.isFile())
        {
            String className = packageName + packageFile.getName().replace(".class", "");
            className = className.replaceAll("/", ".");
            if (!className.contains("$"))
            {
                classNames.addValue(className);
            }
        }
        else
        {
            File[] files = packageFile.listFiles();
            String tmPackageName = packageName + packageFile.getName() + ".";
            for (File f : files)
            {
                findClassNamesByFile(tmPackageName, f, classNames);
            }
        }
    }
    
    /**
     * 读取url下的所有jar，并且返回其中以packagePath开头的className
     * 
     * @param urls
     * @param packagePath
     * @return
     * @throws IOException
     */
    private static void getClassNameByJars(URL[] urls, String packagePath, LightSet<String> classNames)
    {
        if (urls != null)
        {
            for (int i = 0; i < urls.length; i++)
            {
                URL url = urls[i];
                String urlPath = url.getPath();
                String jarPath = urlPath + "!/" + packagePath;
                try
                {
                    URL url2 = new URL(jarPath);
                    getClassNamesByJar(url2, packagePath, classNames);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("url地址：'" + jarPath + "'不正确", e);
                }
            }
        }
    }
    
    /**
     * 进行过滤。过滤规则参照调用方法。
     * 字符串匹配规则参照StringUtil的Match方法
     * 
     * @param filterNames
     * @param classNames
     */
    private static void doFilter(String filterNames, LightSet<String> classNames)
    {
        if (filterNames == null)
        {
            return;
        }
        if (filterNames.startsWith("in~"))
        {
            String[] filters = filterNames.substring(3).split(",");
            for (String filter : filters)
            {
                inFilter(filter, classNames);
            }
        }
        else if (filterNames.startsWith("out~"))
        {
            String[] filters = filterNames.substring(4).split(",");
            for (String filter : filters)
            {
                outFilter(filter, classNames);
            }
        }
        
    }
    
    private static void inFilter(String rule, LightSet<String> classNames)
    {
        for (String each : classNames)
        {
            if (StringUtil.match(each, rule))
            {
                continue;
            }
            else
            {
                classNames.removeValue(each);
            }
        }
    }
    
    private static void outFilter(String rule, LightSet<String> classNames)
    {
        for (String each : classNames)
        {
            if (StringUtil.match(each, rule))
            {
                classNames.removeValue(each);
            }
            else
            {
                continue;
            }
        }
    }
}
