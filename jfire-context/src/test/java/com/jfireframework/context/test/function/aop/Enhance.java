package com.jfireframework.context.test.function.aop;

import javax.annotation.Resource;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.aop.ProceedPoint;
import com.jfireframework.context.aop.ProceedPointImpl;
import com.jfireframework.context.aop.annotation.AfterEnhance;
import com.jfireframework.context.aop.annotation.AroundEnhance;
import com.jfireframework.context.aop.annotation.BeforeEnhance;
import com.jfireframework.context.aop.annotation.EnhanceClass;
import com.jfireframework.context.aop.annotation.ThrowEnhance;

@Resource
@EnhanceClass("com.jfireframework.context.*.aop.Person")
public class Enhance
{
    @BeforeEnhance("sayHello(String)")
    public void sayHello(ProceedPointImpl point)
    {
        point.setPermission(false);
        point.setResult("前置拦截");
    }
    
    @BeforeEnhance("testInts(int[])")
    public void test(ProceedPointImpl point)
    {
        point.setPermission(false);
        point.setResult(new String[0]);
    }
    
    @BeforeEnhance(value = "order()", order = 2)
    public void order3(ProceedPointImpl point)
    {
        point.setPermission(false);
        point.setResult("3");
    }
    
    @BeforeEnhance(value = "order()")
    public void order2(ProceedPointImpl point)
    {
        point.setPermission(false);
        point.setResult("2");
    }
    
    @AfterEnhance("order2(String int)")
    public void order22(ProceedPointImpl point)
    {
        point.setResult("你好");
    }
    
    @AroundEnhance("myName(String)")
    public void testMyname(ProceedPoint point) throws Throwable
    {
        System.out.println("环绕增强钱");
        point.invoke();
        System.out.println("环绕增强后");
    }
    
    @BeforeEnhance(value = "*(*)", order = 10)
    public void all(ProceedPoint point)
    {
        System.out.println("所有方法均会输出");
    }
    
    @ThrowEnhance()
    public void throwe(ProceedPoint point)
    {
        System.out.println("dada");
        Verify.equal("aaaa", point.getE().getMessage(), "捕获到正确的异常");
    }
}
