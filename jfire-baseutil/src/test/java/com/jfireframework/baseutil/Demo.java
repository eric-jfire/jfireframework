package com.jfireframework.baseutil;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Demo
{
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        String value = System.getProperty("java.class.path");
        List<String> jarUrls = new LinkedList<String>();
        for (String each : value.split(";"))
        {
            if (each.endsWith(".jar"))
            {
                JarFile jarFile = null;
                try
                {
                    jarFile =new JarFile(each);
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
                    System.out.println(entryName);
                }
            }
        }
    }
}
