package com.jfireframework.baseutil;

import java.io.IOException;
import java.util.HashMap;
import com.jfireframework.baseutil.reflect.HotswapClassLoader;

public class Demo
{
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        HotswapClassLoader classLoader = new HotswapClassLoader();
        classLoader.setReloadPackages("com.jfireframework");
        Class<?> result1 = classLoader.loadClass("com.jfireframework.baseutil.Logtest");
        Class<?> t = classLoader.loadClass("junit.textui.TestRunner");
        System.out.println(result1);
        System.out.println(result1.getClassLoader());
        System.out.println(t);
        System.out.println(t.getClassLoader());
        classLoader = new HotswapClassLoader( classLoader);
        classLoader.setReloadPackages("com.jfireframework");
         result1 = classLoader.loadClass("com.jfireframework.baseutil.Logtest");
       t = classLoader.loadClass("junit.textui.TestRunner");
        System.out.println(result1);
        System.out.println(result1.getClassLoader());
        System.out.println(t);
        System.out.println(t.getClassLoader());
    }
}
