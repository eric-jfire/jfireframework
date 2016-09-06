package com.jfireframework.baseutil.resource;

public interface ResourceCloseCallback<T>
{
    /**
     * 资源关闭的时候执行的回调动作
     * 
     * @param resource
     */
    public void onClose(T resource);
}
