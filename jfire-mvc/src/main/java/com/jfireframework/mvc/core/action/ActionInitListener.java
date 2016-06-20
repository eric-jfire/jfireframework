package com.jfireframework.mvc.core.action;

public interface ActionInitListener
{
    /**
     * 该方法在action被初始化完成后调用
     */
    public void init(Action action);
}
