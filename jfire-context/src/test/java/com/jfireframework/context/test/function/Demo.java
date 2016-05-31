package com.jfireframework.context.test.function;

import com.jfireframework.baseutil.reflect.HotswapClassLoader;
import com.jfireframework.context.JfireContext;

public class Demo
{
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        HotswapClassLoader classLoader = new HotswapClassLoader();
        classLoader.setReloadPackages("com.jfireframework.context.JfireContextImpl");
        System.out.println(JfireContext.class.getClassLoader());
        Class<?> class1 = classLoader.loadClass("com.jfireframework.context.JfireContextImpl");
        JfireContext jfireContext = (JfireContext) class1.newInstance();
        System.out.println("sdas");
    }
}
