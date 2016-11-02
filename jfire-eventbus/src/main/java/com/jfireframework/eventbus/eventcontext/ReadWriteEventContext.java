package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.eventbus.event.EventConfig;

public interface ReadWriteEventContext<T extends Enum<? extends EventConfig>> extends EventContext<T>
{
    /**
     * 是否直接执行。初始化的时候都是false
     * 
     * @return
     */
    public boolean immediateInvoke();
    
    public static final int READ  = 1;
    public static final int WRITE = 2;
    
    /**
     * 1是读取，2是写入
     * 
     * @return
     */
    public int mode();
    
    public void setImmediateMode();
}
