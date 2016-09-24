package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.eventbus.event.Event;

public interface EventContext
{
    /**
     * 等待直到该事件被处理完成
     */
    public void await();
    
    /**
     * 本事件处理过程中发生了异常，设置异常数据
     * 
     * @param e
     */
    public void setThrowable(Throwable e);
    
    /**
     * 本事件处理过程中捕获的异常
     * 
     * @return
     */
    public Throwable getThrowable();
    
    /**
     * 设置事件处理的结果数据
     * 
     * @param result
     */
    public void setResult(Object result);
    
    /**
     * 获取事件处理的结果数据
     * 
     * @return
     */
    public Object getResult();
    
    /**
     * 
     * 完成该事件，并且（如果有）唤醒等待该事件完成的线程
     */
    public void signal();
    
    /**
     * 等待该事件的完成，最多等待指定的毫秒数
     * 
     * @param mills
     */
    public void await(long mills);
    
    /**
     * 事件是否完成
     * 
     * @return
     */
    public boolean isFinished();
    
    /**
     * 返回事件的待处理数据
     * 
     * @return
     */
    public Object getEventData();
    
    /**
     * 该事件上下文的事件类型
     * 
     * @return
     */
    public Enum<? extends Event<?>> getEvent();
    
}
