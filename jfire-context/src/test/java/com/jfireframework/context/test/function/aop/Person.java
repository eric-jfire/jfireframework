package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.context.aop.annotation.AutoCloseResource;
import com.jfireframework.context.aop.annotation.Transaction;

@Resource
public class Person
{
    private String name = "林斌";
    
    public String sayHello(String word)
    {
        return name + "说" + word;
    }
    
    @Resource(name = "注解保留")
    public void sayHello()
    {
        throw new RuntimeException("自定义错误");
    }
    
    public String[] testInts(int[] ints)
    {
        String[] strs = new String[ints.length];
        for (int i = 0; i < strs.length; i++)
        {
            strs[0] = String.valueOf(ints[i]);
        }
        return strs;
    }
    
    public String order()
    {
        return "1";
    }
    
    public String order2(String name, int age)
    {
        return name + age;
    }
    
    public String myName(String word)
    {
        return name + word;
    }
    
    public void throwe()
    {
        throw new RuntimeException("aaaa");
    }
    
    public void tx()
    {
        System.out.println("数据访问");
        hh();
    }
    
    @Transaction
    protected void hh()
    {
        System.out.println("dsada");
    }
    
    @AutoCloseResource
    public void autoClose()
    {
        String name = "12";
        System.out.println("自动关闭");
    }
    
}
