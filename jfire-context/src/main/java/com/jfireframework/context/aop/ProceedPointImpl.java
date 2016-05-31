package com.jfireframework.context.aop;

/**
 * 用于在AOP增强中对连接点的抽象。
 * 
 * @author linbin
 * 
 */
public class ProceedPointImpl implements ProceedPoint
{
    // 目标类对象实例
    protected Object    host       = null;
    // 目标类执行后的返回结果实例
    protected Object    result     = null;
    // 目标方法抛出的异常
    protected Throwable e          = null;
    // 目标方法执行许可，用于前置增强中，如果为false，则原方法不被执行
    protected boolean   permission = true;
    // 目标方法的参数数组
    protected Object[]  param      = new Object[0];
    
    /**
     * 表示对目标方法的调用。在静态代码中作为继承方法被修改以实现对目标方法的调用
     * 
     * @return
     */
    @Override
    public Object invoke() throws Throwable
    {
        throw new RuntimeException("该方法只在环绕增强方法中可被调用，其余情况均异常");
    }
    
    @Override
    public Object getHost()
    {
        return host;
    }
    
    public void setHost(Object host)
    {
        this.host = host;
    }
    
    @Override
    public Throwable getE()
    {
        return e;
    }
    
    public void setE(Throwable e)
    {
        this.e = e;
    }
    
    public boolean isPermission()
    {
        return permission;
    }
    
    @Override
    public void setPermission(boolean permission)
    {
        this.permission = permission;
    }
    
    @Override
    public Object getResult()
    {
        return result;
    }
    
    public void setResult(Object invokedResult)
    {
        this.result = invokedResult;
    }
    
    @Override
    public Object[] getParam()
    {
        return param;
    }
    
    public void setParam(Object[] param)
    {
        this.param = param;
    }
    
}
