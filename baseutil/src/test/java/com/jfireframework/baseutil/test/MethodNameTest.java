package com.jfireframework.baseutil.test;

import java.lang.reflect.Method;
import org.junit.Test;
import com.jfireframework.baseutil.data.ChildObject;
import com.jfireframework.baseutil.reflect.ReflectUtil;

public class MethodNameTest
{
    @Test
    public void test()
    {
        Method[] methods = ReflectUtil.getAllMehtods(ChildObject.class);
        for (Method each : methods)
        {
            System.out.println(each.toString());
        }
    }
}
