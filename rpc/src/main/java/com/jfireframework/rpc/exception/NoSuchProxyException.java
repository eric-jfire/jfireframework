package com.jfireframework.rpc.exception;

public class NoSuchProxyException extends LbrcException
{

    /**
     * 
     */
    private static final long serialVersionUID = 4128404531600649547L;

    public NoSuchProxyException(String msg)
    {
        super("代理："+msg+" 不存在");
    }
    
}
