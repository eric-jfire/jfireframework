package com.jfireframework.rpc.exception;

public class NoSuchMethodException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = -2300880187419948816L;

    public NoSuchMethodException(String methodName)
    {
        super("不存在" + methodName + "这样的方法，请检查调用程序是否拼写错误");
    }
}
